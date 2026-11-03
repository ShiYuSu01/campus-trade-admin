package com.campus.trade.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.campus.trade.dao.AuditLogDao;
import com.campus.trade.dao.OrderDao;
import com.campus.trade.dao.ProductDao;
import com.campus.trade.dao.ReportDao;
import com.campus.trade.dao.UserDao;
import com.campus.trade.dto.CategoryStat;
import com.campus.trade.dto.DashboardData;
import com.campus.trade.dto.DashboardStats;
import com.campus.trade.dto.TrendPoint;

/**
 * 数据看板聚合服务。
 */
public class DashboardService {

    private final UserDao userDao = new UserDao();
    private final ProductDao productDao = new ProductDao();
    private final OrderDao orderDao = new OrderDao();
    private final ReportDao reportDao = new ReportDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public DashboardData load() throws Exception {
        DashboardData data = new DashboardData();

        DashboardStats stats = new DashboardStats();
        stats.setUserCount(userDao.countUsers());
        stats.setProductCount(productDao.countAll());
        stats.setPendingProductCount(productDao.countByStatus("PENDING"));
        stats.setOrderCount(orderDao.countAll());
        stats.setPendingReportCount(reportDao.countByStatus("PENDING"));
        stats.setTotalGmv(orderDao.sumCompletedAmount());
        stats.setTodayOrderCount(orderDao.countToday());
        stats.setTodayUserCount(userDao.countToday());
        stats.setTotalReportCount(reportDao.countAll());
        stats.setResolvedReportCount(reportDao.countResolved());
        data.setStats(stats);

        data.setTrend(buildTrend(orderDao.trend(7)));
        data.setCategories(buildCategoryStats(productDao.categoryStats(), stats.getProductCount()));
        data.setPendingProducts(productDao.findTopPending(5));
        data.setPendingReports(reportDao.findPending(5));
        data.setRecentOrders(orderDao.findRecent(6));
        data.setRecentLogs(auditLogDao.findRecent(8));
        return data;
    }

    private List<TrendPoint> buildTrend(List<TrendPoint> raw) {
        Map<String, TrendPoint> indexed = new HashMap<>();
        for (TrendPoint point : raw) {
            indexed.put(point.getDate(), point);
        }
        List<TrendPoint> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter keyFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter labelFormat = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String key = keyFormat.format(date);
            TrendPoint point = indexed.get(key);
            if (point == null) {
                point = new TrendPoint();
                point.setDate(key);
                point.setCount(0);
                point.setAmount(java.math.BigDecimal.ZERO);
            }
            point.setLabel(labelFormat.format(date));
            result.add(point);
        }
        long max = result.stream().mapToLong(TrendPoint::getCount).max().orElse(0);
        for (TrendPoint point : result) {
            if (max == 0 || point.getCount() == 0) {
                point.setPercent(0);
            } else {
                point.setPercent(Math.max(6, (int) Math.round(point.getCount() * 100.0 / max)));
            }
        }
        return result;
    }

    private List<CategoryStat> buildCategoryStats(List<CategoryStat> raw, long totalProducts) {
        for (CategoryStat stat : raw) {
            if (totalProducts == 0 || stat.getCount() == 0) {
                stat.setPercent(0);
            } else {
                stat.setPercent(Math.max(3, (int) Math.round(stat.getCount() * 100.0 / totalProducts)));
            }
        }
        return raw;
    }
}
