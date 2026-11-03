package com.campus.trade.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 近 7 天订单趋势中的一个点。
 */
public class TrendPoint {

    private String date;
    private String label;
    private long count;
    private BigDecimal amount = BigDecimal.ZERO;
    private int percent;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount == null ? BigDecimal.ZERO : amount;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getAmountText() {
        return "¥" + amount.setScale(0, RoundingMode.HALF_UP);
    }
}
