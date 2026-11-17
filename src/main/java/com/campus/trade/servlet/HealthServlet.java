package com.campus.trade.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import com.campus.trade.common.DbUtil;
import com.campus.trade.dto.HealthStatus;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 健康检查接口：GET /health
 * 用途：部署完成后一条命令确认「应用已启动」且「数据库可连通」，便于排查是容器问题还是数据库问题。
 * 说明：挂在 /health（不在 /admin/* 下），不经过登录过滤器，只暴露版本与连通性，不暴露任何业务数据。
 */
@WebServlet("/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");

        boolean ok;
        String message;
        long cost = -1;
        try {
            long begin = System.currentTimeMillis();
            try (Connection connection = DbUtil.getConnection()) {
                ok = connection.isValid(2);
                message = ok ? "MySQL 连接正常" : "MySQL 连接无效";
            }
            cost = System.currentTimeMillis() - begin;
        } catch (Exception ex) {
            ok = false;
            message = "MySQL 连接失败: " + ex.getMessage();
        }

        HealthStatus status = new HealthStatus()
                .status(ok ? "UP" : "DEGRADED")
                .serverInfo(getServletContext().getServerInfo())
                .contextPath(getServletContext().getContextPath())
                .database(ok, message, cost);

        if (!ok) {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        try (PrintWriter out = resp.getWriter()) {
            out.print(status.toJson());
        }
    }
}
