package com.example.test.util;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Jsoup带着cookie下载验证码到本地(必须带着cookie下载验证码，否则下载的验证码无效)
 */
public class DownLoadImg {

    /**
     * 带着cookie下载验证码图片
     */
    public static void downloadImg(String url, Map<String, String> cookies) throws IOException {
        Connection connect = Jsoup.connect(url);
        connect.cookies(cookies);// 携带cookies爬取图片
        connect.timeout(5 * 10000);
        connect.header("Host", "121.248.70.120").header("Referer", "http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx");
        Connection.Response response = connect.ignoreContentType(true).execute();
        byte[] img = response.bodyAsBytes();
        savaImage(img, Environment.getExternalStorageDirectory().getPath()+"/picture/", "yzm.png");
    }

    /**
     * 保存图片到本地
     */
    public static void savaImage(byte[] img, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        File dir = new File(filePath);
        try {
            //判断文件目录是否存在
            if (dir.exists() && !dir.isDirectory()) {
                dir.delete();
            }
            dir.mkdir();
            file = new File(filePath + "" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
