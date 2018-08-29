package com.asiainfom.commons.sftputils.utils;

import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author king-pan
 * Date: 2018/8/24
 * Time: 下午2:11
 * Description: No Description
 */
public class DateUtils {

    public static String getMonthText(){
        DateTime dateTime = new DateTime();
        return dateTime.minusDays(1).toString("yyyy-MM");
    }

    public static String getBeforeMonthText(){
        DateTime dateTime = new DateTime();
        return dateTime.minusMonths(1).toString("yyyyMM");
    }

    public static String getDayText(){
        DateTime dateTime = new DateTime();
        return dateTime.minusDays(1).toString("yyyyMMdd");
    }

    public static void main(String[] args) {
        System.out.println(getMonthText());
        System.out.println(getDayText());
        System.out.println(getBeforeMonthText());
    }
}
