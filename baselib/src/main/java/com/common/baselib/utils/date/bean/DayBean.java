package com.common.baselib.utils.date.bean;

import com.common.baselib.utils.date.DateUtil;

import java.io.Serializable;
import java.math.BigDecimal;


public class DayBean implements Serializable {

    public boolean isOffsetDate;

    public int state;

    public int year;
    public int mouth;
    public int day;

    public BigDecimal price;

    public Object extraObj;

    public DayBean(int year, int mouth, int day) {
        this.year = year;
        this.mouth = mouth;
        this.day = day;
    }

    public void setOffsetDate(boolean offsetDate) {
        isOffsetDate = offsetDate;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "DayBean{" +
                "isOffsetDate=" + isOffsetDate +
                ", year=" + year +
                ", mouth=" + mouth +
                ", day=" + day +
                '}';
    }

    public boolean isSameDay(DayBean dayBean) {
        if (dayBean == null) {
            return false;
        }
        return dayBean.day == day && dayBean.mouth == mouth && dayBean.year == year;
    }

    public boolean isCanClick() {
        return !isOffsetDate && (state == DateUtil.TODAY || state == DateUtil.TODAY_AFTER);
    }

    public boolean isShow() {
        return !isOffsetDate;
    }

    public String formatDate() {
        return year + "-" + DateUtil.getStrNum(mouth) + "-" + DateUtil.getStrNum(day);
    }

    public long getDateNum() {
        try {
            return DateUtil.yyyy_MM_dd.parse(formatDate()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }

}
