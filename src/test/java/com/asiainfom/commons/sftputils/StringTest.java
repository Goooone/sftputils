package com.asiainfom.commons.sftputils;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author king-pan
 * Date: 2018/9/5
 * Time: 上午11:07
 * Description: No Description
 */
public class StringTest {


    @Test
    public void test(){
        String str = "11111${month}2222";
        System.out.println(str.indexOf("${month}"));
    }
}
