package com.campus.trade.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campus.trade.dto.DashboardData;
import com.campus.trade.service.DashboardService;
import com.campus.trade.web.BaseServlet;

/**
 * 后台数据看板。
 */
@WebServlet("/admin/dashboard")
public class DashboardServlet extends BaseServlet {

    private final DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            DashboardData data = dashboardService.load();
            req.setAttribute("data", data);
            req.setAttribute("pageTitle", "数据看板");
            req.setAttribute("pageSubtitle", "平台运行概览与今日待办");
            req.setAttribute("activeMenu", "dashboard");
            forward(req, resp, "dashboard.jsp");
        } catch (Exception ex) {
            throw new ServletException("加载数据看板失败", ex);
        }
    }
}
