package com.common.baselib.utils.date.bean;

import java.util.List;


public class MouthBean {

    public int row;

    public int year;
    public int mouth;
    public List<DayBean> dateList;

    public MouthBean(int year, int mouth, List<DayBean> dateList) {
        this.year = year;
        this.mouth = mouth;
        this.dateList = dateList;
    }

    @Override
    public String toString() {
        return "MouthBean{" +
                "year=" + year +
                ", mouth=" + mouth +
                ", dateList=" + dateList +
                '}';
    }
}
