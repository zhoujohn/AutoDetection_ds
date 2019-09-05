package com.common.baselib.utils.date;

import com.common.baselib.utils.date.bean.DayBean;
import com.common.baselib.utils.date.bean.MouthBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

public final class DateUtil {

    public static HashMap<Integer, String> weekMap = new HashMap<>();

    static {
        weekMap.put(1, "周一");
        weekMap.put(2, "周二");
        weekMap.put(3, "周三");
        weekMap.put(4, "周四");
        weekMap.put(5, "周五");
        weekMap.put(6, "周六");
        weekMap.put(0, "周日");
    }

    public static final int EMPTY = -1;
    public static final int TODAY_BEFORE = 1;
    public static final int TODAY = 2;
    public static final int TODAY_AFTER = 3;

    public static final int DAY_OF_WEEK = 7;
    public static final int MOUTH_OF_YEAR = 12;

    public static SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat MM_dd = new SimpleDateFormat("MM-dd");
    public static SimpleDateFormat yyyy_MM_dd_HHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat MM_dd_HHmm = new SimpleDateFormat("MM-dd HH:mm");
    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat HH_mm = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat HH_mm_ss = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat MM_dd_HAN = new SimpleDateFormat("MM月dd日");
    public static SimpleDateFormat yyyyMMdd_HHmm = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    public static long SEC = 1000;
    public static long MIN = 60 * 1000;
    public static long HOUR = 60 * MIN;
    public static long DAY = 24 * HOUR;


    /**
     * 根据 年、月 获取对应的月份 的 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取当前您月日
     *
     * @return
     */
    public static String getCurrDateSS() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);
        String mon;
        if (m < 10) {
            mon = "0" + m;
        } else {
            mon = "" + m;
        }

        return y + "/" + mon + "/" + d + "/";
    }

    /**
     * 获取当前您月日
     *
     * @return
     */
    public static DayBean getCurrDate() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);
        DayBean dayBean = new DayBean(y, m, d);
        return dayBean;
    }

    /**
     * 获取当前您月日
     *
     * @return
     */
    public static String getCurrDateStr() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);
        String mon;
        if (m < 10) {
            mon = "0" + m;
        } else {
            mon = "" + m;
        }

        return "" + y + mon + d;
    }


    public static int getCurrYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 根据日期获取星期
     *
     * @param datetime
     * @return
     */
    public static int dateToWeek(String datetime) {
        Calendar cal = Calendar.getInstance();
        Date datet = null;
        try {
            datet = yyyy_MM_dd.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    public static int dateToWeek(long datetime) {
        Calendar cal = Calendar.getInstance();
        Date datet = new Date(datetime);
        cal.setTime(datet);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    public static int dateToWeek(DayBean dayBean) {
        return dateToWeek(dayBean.year + "-" + dayBean.mouth + "-" + dayBean.day);
    }


    /**
     * 获取日历信息
     *
     * @param yearStart
     * @param mouthStart
     * @param mouthCount
     */
    public static List<MouthBean> getMouthList(int yearStart, int mouthStart, int mouthCount) {

        int mouthEnd = mouthStart + mouthCount;
        List<MouthBean> mouthList = new ArrayList<>();
        for (int mouth = mouthStart; mouth <= mouthEnd; mouth++) {

            int yearCount = 0;
            int mouthCurr = mouth % MOUTH_OF_YEAR;
            if (mouth % MOUTH_OF_YEAR == 0) {
                yearCount = mouth / MOUTH_OF_YEAR - 1;
                mouthCurr = MOUTH_OF_YEAR;
            } else {
                yearCount = mouth / MOUTH_OF_YEAR;
            }
            if (yearCount < 0) {
                yearCount = 0;
            }
            int yearCurr = yearStart + yearCount;


            List<DayBean> list = new ArrayList<>();
            MouthBean mouthBean = new MouthBean(yearCurr, mouthCurr, list);

            int count = getDaysByYearMonth(yearCurr, mouthCurr);
            String dateStr = yearCurr + "-" + mouthCurr + "-" + 1;
            int week = dateToWeek(dateStr);

            int offset = week;

            int totalCount = count + offset;
            int rowCount = totalCount % DAY_OF_WEEK == 0 ? (totalCount / DAY_OF_WEEK) : (totalCount / DAY_OF_WEEK) + 1;
            int totalCountNoOffset = rowCount * DAY_OF_WEEK;

            mouthBean.row = rowCount;

            for (int day = 0; day < totalCountNoOffset; day++) {
                DayBean dateBean = new DayBean(yearCurr, mouthCurr, day + 1 - week);
                dateBean.setOffsetDate(day < week || day >= totalCount);
                dateBean.setState(getDayState(dateBean));
                list.add(dateBean);
            }

            mouthList.add(mouthBean);
        }
        return mouthList;
    }


    public static int getDayState(DayBean dayBean) {
        if (dayBean.isOffsetDate) {
            return EMPTY;
        } else {
            Calendar cal = Calendar.getInstance();

            int yToday = cal.get(Calendar.YEAR);
            int mToday = cal.get(Calendar.MONTH) + 1;
            int dToday = cal.get(Calendar.DATE);

            if (yToday == dayBean.year && mToday == dayBean.mouth && dToday == dayBean.day) {
                return TODAY;
            } else {
                if (yToday > dayBean.year) {
                    return TODAY_BEFORE;
                } else if (yToday == dayBean.year && mToday > dayBean.mouth) {
                    return TODAY_BEFORE;
                } else if (yToday == dayBean.year && mToday == dayBean.mouth && dToday > dayBean.day) {
                    return TODAY_BEFORE;
                } else {
                    return TODAY_AFTER;
                }
            }
        }
    }

    public static String getStrNum(int num) {
        return num < 10 ? "0" + num : String.valueOf(num);
    }


    /**
     * 格式化时间
     *
     * @param timeStr
     * @return
     */
    public static String formatTime(String timeStr) {
        return formatTime(timeStr, "%dh%smin");
    }

    public static String formatTime(String timeStr, String format) {
        try {
            int time = Integer.parseInt(timeStr);
            int hour = time / 60;
            int min = time % 60;
            return String.format(format, hour, getStrNum(min));
        } catch (Exception e) {
            return String.format(format, 0, 0);
        }
    }

    /**
     * 将秒 转换为 时分秒
     *
     * @param timeMs
     * @return
     */
    public static String stringForTime(long timeMs) {
        long seconds = timeMs % 60;
        long minutes = (timeMs / 60) % 60;
        long hours = timeMs / 3600;

        return new Formatter().format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }


    public static long getTime(long serverTime) {
        long currTime = System.currentTimeMillis();
        long timeOut = serverTime - currTime;
        if (timeOut < 0) {
            return -1;
        } else {
            return timeOut / 1000;
        }
    }

    public static String format(SimpleDateFormat format, long time) {
        try {
            return format.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String formatStyle2(long time) {
        String dateStr = DateUtil.format(DateUtil.MM_dd_HAN, time);
        String weekStr = DateUtil.weekMap.get(DateUtil.dateToWeek(time));
        return dateStr + "  " + weekStr;
    }

    static String[][] constellations = {{"摩羯座", "水瓶座"}, {"水瓶座", "双鱼座"}, {"双鱼座", "白羊座"}, {"白羊座", "金牛座"}, {"金牛座", "双子座"}, {"双子座", "巨蟹座"}, {"巨蟹座", "狮子座"},
            {"狮子座", "处女座"}, {"处女座", "天秤座"}, {"天秤座", "天蝎座"}, {"天蝎座", "射手座"}, {"射手座", "摩羯座"}};
    //星座分割时间
    static int[] date = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};

    //星座生成 传进是日期格式为: yyyy-mm-dd
    public static String getConstellations(String birthday) {
        String[] data = birthday.split("-");
        int day = date[Integer.parseInt(data[1]) - 1];
        String[] cl1 = constellations[Integer.parseInt(data[1]) - 1];
        if (Integer.parseInt(data[2]) >= day) {
            return cl1[1];
        } else {
            return cl1[0];
        }
    }

    /**
     * 获取时间间隔
     *
     * @param millisecond
     * @return
     */
    public static String getSpaceTime(Long millisecond) {
        Long currentMillisecond = Long.valueOf(getTimeStame());

        //间隔秒
        Long spaceSecond = currentMillisecond - millisecond;//相差多少毫秒
        //1分钟
        long time = 1000 * 60;
        //一小时
        long hour = time * 60;
        //一天
        long day = hour * 24;
        //十天
        long tenday = day * 10;
        //一分钟之内
        //修复那不到"刚刚"的标签，因为会返回负的毫秒值
        //if (spaceSecond >= 0 && spaceSecond <= time) {//一分钟
        if (spaceSecond <= time) {//一分钟
            return "刚刚";
        }
        //一小时之内
        else if (spaceSecond > time && spaceSecond <= hour) {//一小时
            return ((int) (spaceSecond / time)) + "分钟前";
        }
        //一天之内
        else if (spaceSecond > hour && spaceSecond <= day) {//一天
            return ((int) (spaceSecond / hour)) + "小时前";
        }
        //3天之内
        else if (spaceSecond > day && spaceSecond <= tenday) {//10天
            return ((int) (spaceSecond / day)) + "天前";
        } else {
            return getDateTimeFromMillisecond(millisecond);
        }
    }


    public static String getTimeStame() {
        //获取当前的毫秒值
        long time = System.currentTimeMillis();
        //将毫秒值转换为String类型数据
        String time_stamp = String.valueOf(time);
        //返回出去
        return time_stamp;
    }

    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    public static String getDateTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

}
