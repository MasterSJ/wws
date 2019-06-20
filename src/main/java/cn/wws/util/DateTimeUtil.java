package cn.wws.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    public static final SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    public static String getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        Date monday = cal.getTime();
        synchronized(sdfd){
            String strMonday = sdfd.format(monday);
            return strMonday;
        }
    }
    
    public static String getFirstDayOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        if(dayOfMonth > 1){
            cal.add(Calendar.DATE, 1 - dayOfMonth);
        }
        Date first = cal.getTime();
        synchronized(sdfd){
            return sdfd.format(first);
        }
    }
    
    public static String getFirstDayOfYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        if(dayOfYear > 1){
            cal.add(Calendar.DATE, 1 - dayOfYear);
        }
        Date first = cal.getTime();
        synchronized(sdfd){
            return sdfd.format(first);
        }
    }
    
    public static String getCurrentTime() {
        Date time = new Date();
        synchronized(sdft){
            return sdft.format(time);
        }
    }
    
    public static String getCurrentDate() {
        Date time = new Date();
        synchronized(sdfd){
            return sdfd.format(time);
        }
    }
    
    public static String getCurrentMonthDay() {
        String date = getCurrentDate();
        return date.substring(5);
    }
    
    public static String getSeveralDaysLater(int several) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, several);
        Date monday = cal.getTime();
        synchronized(sdfd){
            String strDate = sdfd.format(monday);
            return strDate;
        }
    }
    
    public static String getCurrentTimeUTC() {
        Date time = new Date();
        synchronized(sdfUTC){
            return sdfUTC.format(time);
        }
    }
    
    //返回毫秒数
    public static long parseTime(String time, SimpleDateFormat format) throws ParseException {
        synchronized(format){
            return format.parse(time).getTime();
        }
    }
}
