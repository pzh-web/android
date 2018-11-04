package com.example.test.util;

import android.widget.Toast;

import com.example.test.entity.CourseInfo;
import com.example.test.service.DBService;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fetcher {
    /**
     * 第一次访问获取的cookie(查看发现就返回一个cookie:ASP.NET_SessionId)
     */
    private Map<String, String> cookies = null;
    private DBService dbService;

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Fetcher(DBService dbService) {
        this.cookies = new HashMap<String, String>();
        this.dbService = dbService;
    }

    public void fetch() {
        String urlLogin = "http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx";
        Connection connect = Jsoup.connect(urlLogin);

        // 请求url获取响应信息
        Response res = null;
        try {
            res = connect.ignoreContentType(true).method(Method.GET).execute();
            // 获取返回的cookie
            cookies = res.cookies();
            // 抓起验证码
            DownLoadImg.downloadImg("http://121.248.70.120/jwweb/sys/ValidateCode.aspx", cookies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 抓取教师信息
     */
    public List<String> fetchTeacherInfo() {
        // 查询数据库中是否已有
        List<String> list = dbService.queryTeacher();
        if (list.size() == 0) {
            String urlLogin = "http://121.248.70.120/jwweb/ZNPK/Private/List_JS.aspx?xnxq=20180&t=286";
            try {
                Connection connect = Jsoup.connect(urlLogin);
                connect.timeout(5 * 100000);
                // 伪造请求头
                connect.header("Host", "121.248.70.120").header("Referer", "http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx");
                connect.cookies(this.getCookies());
                Document document = connect.post();
                Element script = document.select("script").get(0);
                String html = script.html();
                String select = html.substring(html.indexOf("'") + 1, html.lastIndexOf("'"));

                document = Jsoup.parse(select);
                Elements elements = document.select("option");
                for (int i = 0; i < elements.size(); i++) {
                    if (i == 0)
                        list.add("选择一位教师");
                    else {
                        Element element = elements.get(i);
                        String teacherId = element.attr("value");
                        String name = element.text();
                        list.add(name);
                        // 保存到数据库
                        this.dbService.addTeacher(teacherId, name);
                    }
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 抓取老师的课程信息
     */
    public List<CourseInfo> fetchTeacherCourse(String teacherName, String yzm, String week) {
        // 判断数据库中是否存在老师课程信息
        String teacherId = dbService.queryTeacherIdByName(teacherName);
        List<CourseInfo> courseInfos = dbService.queryCourseInfoByTeacherIdAndWeek(teacherId, week);
        // 爬取数据并保存数据
        if (courseInfos.size() <= 0) {
            try {

                String urlLogin = "http://121.248.70.120/jwweb/ZNPK/TeacherKBFB_rpt.aspx";
                Connection connect = Jsoup.connect(urlLogin);
                connect.timeout(5 * 1000000);
                connect.header("Host", "121.248.70.120").header("Referer", "http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx");
                // 携带登陆信息
                connect.data("Sel_XNXQ", "20180").data("Sel_JS", teacherId).data("type", "1").data("txt_yzm", yzm);
                connect.cookies(this.getCookies());
                // 请求url获取响应信息
                Response res = connect.ignoreContentType(true).method(Method.POST).execute();// 执行请求
                String body = res.body();
                Document document = Jsoup.parse(body);
                Element element = document.getElementById("pageRpt");
                if (element.getElementsByTag("tr").size() != 1)
                    parseHtml(element, teacherId);
//                else{
////                    dbService.addCourse(generateCourseInfo(teacherId, null, null, null, null, null, null, 1));
////                }

                // 在获取一次
                courseInfos = dbService.queryCourseInfoByTeacherIdAndWeek(teacherId, week);
                return courseInfos;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return courseInfos;
    }

    private void parseHtml(Element element, String teacherId) {
        CourseInfo courseInfo = null;
        // 获取课程信息table
        Element table = element.getElementsByTag("table").get(3);
        // 获取课程信息tr
        Elements tr = table.getElementsByTag("tr");
        for (int i = 1; i < tr.size(); i++) {
            // 获取课程信息td
            Elements td = tr.get(i).getElementsByTag("td");
            if (i == 1 || i == 3) {
                for (int j = 2; j < td.size(); j++) {
                    String html = td.get(j).html();
                    if (html.equals("<br>")) {
                        courseInfo = generateCourseInfo(teacherId, i, null, null, null, null, j - 1, null);
                        dbService.addCourse(courseInfo);
                    } else {
                        parseTeacherInfo(html, teacherId, j - 1);
                    }
                }
            } else {
                for (int k = 1; k < td.size(); k++) {
                    String html = td.get(k).html();
                    if (html.equals("<br>")) {
                        courseInfo = generateCourseInfo(teacherId, i, null, null, null, null, k, null);
                        dbService.addCourse(courseInfo);
                    } else {
                        parseTeacherInfo(html, teacherId, k);
                    }
                }
            }
        }
    }

    private void parseTeacherInfo(String teacherInfo, String teacherId, int weekTime) {
        String courseName = null;
        String classRoom = null;
        String className = null;
        Integer number = null;
        Integer courseSection = null;
        String[] courses = teacherInfo.split("<br>");
        if (courses.length > 2) {
            courseName = courses[0].trim();
            classRoom = courses[2].split("；")[0].trim();
            className = courses[2].split("；")[1].trim();
            number = Integer.valueOf(courses[3].split("：")[1].trim());
            courseSection = generateCourseSection(courses[1].substring(courses[1].lastIndexOf("]") + 1).trim());
            batchAddCourseInfoByWeek(teacherId, courseSection, courseName, classRoom, className, number, weekTime, courses[1].substring(courses[1].indexOf("[") + 1, courses[1].lastIndexOf("]")));
        } else {
            String[] str = courses[0].split(" ");
            number = Integer.valueOf(courses[1].split("：")[1].trim());
            courseName = str[0].trim();
            classRoom = str[2].trim();
            className = str[4] + str[3];
            courseSection = generateCourseSection(str[1].substring(str[1].lastIndexOf("]") + 1).trim());
            batchAddCourseInfoByWeek(teacherId, courseSection, courseName, classRoom, className, number, weekTime, str[1].substring(str[1].indexOf("[") + 1, str[1].lastIndexOf("]")));
        }
    }

    private void batchAddCourseInfoByWeek(String teacherId, Integer courseSection, String courseName, String classRoom, String className, Integer number, int weekTime, String weekStr) {
        String[] weeks = weekStr.substring(0, weekStr.lastIndexOf("周")).split("-");
        CourseInfo courseInfo = null;
        for (int i = Integer.valueOf(weeks[0]); i <= Integer.valueOf(weeks[1]); i++) {
            courseInfo = generateCourseInfo(teacherId, courseSection, courseName, classRoom, className, number, weekTime, i);
            dbService.addCourse(courseInfo);
        }
    }

    private Integer generateCourseSection(String str) {
        if (str.equals("1-2节"))
            return 1;
        else if (str.equals("3-4节"))
            return 2;
        else if (str.equals("5-6节"))
            return 3;
        else if (str.equals("7-8节"))
            return 4;
        else
            return 5;
    }

    private CourseInfo generateCourseInfo(String teacherId, Integer courseSection, String courseName, String classRoom, String className, Integer number, Integer weekTime, Integer week) {
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setTeacherId(teacherId);
        courseInfo.setCourseSection(courseSection);
        courseInfo.setCourseName(courseName);
        courseInfo.setClassRoom(classRoom);
        courseInfo.setClassName(className);
        courseInfo.setNumber(number);
        courseInfo.setWeekTime(weekTime);
        courseInfo.setWeek(week);
        return courseInfo;
    }
}
