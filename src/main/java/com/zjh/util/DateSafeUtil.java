package com.zjh.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author:jinhui.zhao
 * @description:
 * @date: created in 上午10:09 2018/9/27
 */
public class DateSafeUtil {

    private static final String yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss";

    private static ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = new ThreadLocal<>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(yyyyMMdd_HHmmss);
        }
    };

    public static  String formatDate(Date date)throws ParseException {
        return simpleDateFormatThreadLocal.get().format(date);
    }

    public static Date parse(String strDate) throws ParseException{
        return simpleDateFormatThreadLocal.get().parse(strDate);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        var instant = date.toInstant();
        var zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        var zone = ZoneId.systemDefault();
        var instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /*public static class TestSimpleDateFormatThreadSafe extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    this.join(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                try {
                    System.out.println(this.getName()+":"+DateSafeUtil.parse("2013-05-24 06:02:20"));
                } catch (ParseException e) {
                    e.printStacgitkTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        for(int i = 0; i < 3; i++){
            new TestSimpleDateFormatThreadSafe().start();
        }

    }*/

}
