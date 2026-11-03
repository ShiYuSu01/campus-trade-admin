package com.campus.trade.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import com.campus.trade.common.DateTimeUtil;
import com.campus.trade.service.ProductService;
import com.campus.trade.service.ReportService;

/**
 * 给后台所有页面准备侧边栏待办数量，避免每个 Servlet 重复查询。
 */
public class LayoutFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(LayoutFilter.class.getName());
    private final ProductService productService = new ProductService();
    private final ReportService reportService = new ReportService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            request.setAttribute("navPendingProducts", productService.countPending());
            request.setAttribute("navPendingReports", reportService.countPending());
            request.setAttribute("todayText", DateTimeUtil.todayText());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "读取侧边栏待办数量失败", ex);
            request.setAttribute("navPendingProducts", 0L);
            request.setAttribute("navPendingReports", 0L);
            request.setAttribute("todayText", DateTimeUtil.todayText());
        }
        chain.doFilter(request, response);
    }
}
