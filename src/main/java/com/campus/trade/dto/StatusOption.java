package com.campus.trade.dto;

/**
 * 订单详情页可选的下一个状态。
 */
public class StatusOption {

    private final String value;
    private final String label;

    public StatusOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
