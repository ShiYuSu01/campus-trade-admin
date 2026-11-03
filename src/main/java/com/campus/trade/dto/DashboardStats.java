package com.campus.trade.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数据看板顶部统计卡片。
 */
public class DashboardStats {

    private long userCount;
    private long productCount;
    private long pendingProductCount;
    private long orderCount;
    private long pendingReportCount;
    private BigDecimal totalGmv = BigDecimal.ZERO;
    private long todayOrderCount;
    private long todayUserCount;
    private long totalReportCount;
    private long resolvedReportCount;

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    public long getPendingProductCount() {
        return pendingProductCount;
    }

    public void setPendingProductCount(long pendingProductCount) {
        this.pendingProductCount = pendingProductCount;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public long getPendingReportCount() {
        return pendingReportCount;
    }

    public void setPendingReportCount(long pendingReportCount) {
        this.pendingReportCount = pendingReportCount;
    }

    public BigDecimal getTotalGmv() {
        return totalGmv;
    }

    public void setTotalGmv(BigDecimal totalGmv) {
        this.totalGmv = totalGmv == null ? BigDecimal.ZERO : totalGmv;
    }

    public long getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(long todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public long getTodayUserCount() {
        return todayUserCount;
    }

    public void setTodayUserCount(long todayUserCount) {
        this.todayUserCount = todayUserCount;
    }

    public long getTotalReportCount() {
        return totalReportCount;
    }

    public void setTotalReportCount(long totalReportCount) {
        this.totalReportCount = totalReportCount;
    }

    public long getResolvedReportCount() {
        return resolvedReportCount;
    }

    public void setResolvedReportCount(long resolvedReportCount) {
        this.resolvedReportCount = resolvedReportCount;
    }

    public String getGmvText() {
        return "¥" + totalGmv.setScale(2, RoundingMode.HALF_UP);
    }

    public int getResolutionRate() {
        if (totalReportCount == 0) {
            return 0;
        }
        return (int) Math.round(resolvedReportCount * 100.0 / totalReportCount);
    }
}
