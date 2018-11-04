package com.example.test;

import com.example.test.util.Fetcher;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testFetch() {
        String str= "动物解剖生理 [1-20周]1-2节 M1-A103 ；18畜牧兽医3 18畜牧兽医4<br>人数：82<br>";
        System.out.println(str.split("<br>").length);
        for(String s:str.split(" "))
            System.out.println(s);
    }
}