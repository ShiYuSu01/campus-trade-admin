package com.campus.trade.dto;

import java.util.List;
import com.campus.trade.entity.AuditLog;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.Report;

/**
 * 数据看板页面数据。
 */
public class DashboardData {

    private DashboardStats stats;
    private List<TrendPoint> trend;
    private List<CategoryStat> categories;
    private List<Product> pendingProducts;
    private List<Report> pendingReports;
    private List<Order> recentOrders;
    private List<AuditLog> recentLogs;

    public DashboardStats getStats() {
        return stats;
    }

    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }

    public List<TrendPoint> getTrend() {
        return trend;
    }

    public void setTrend(List<TrendPoint> trend) {
        this.trend = trend;
    }

    public List<CategoryStat> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryStat> categories) {
        this.categories = categories;
    }

    public List<Product> getPendingProducts() {
        return pendingProducts;
    }

    public void setPendingProducts(List<Product> pendingProducts) {
        this.pendingProducts = pendingProducts;
    }

    public List<Report> getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(List<Report> pendingReports) {
        this.pendingReports = pendingReports;
    }

    public List<Order> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<Order> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<AuditLog> getRecentLogs() {
        return recentLogs;
    }

    public void setRecentLogs(List<AuditLog> recentLogs) {
        this.recentLogs = recentLogs;
    }
}
