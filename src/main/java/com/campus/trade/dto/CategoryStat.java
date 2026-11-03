package com.campus.trade.dto;

/**
 * 商品分类占比。
 */
public class CategoryStat {

    private String name;
    private long count;
    private int percent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
