package com.campus.trade.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.WebUtil;
import com.campus.trade.entity.AuditLog;
import com.campus.trade.service.AuditLogService;
import com.campus.trade.web.BaseServlet;

/**
 * 审核日志查询。
 */
@WebServlet("/admin/audit-logs")
public class AuditLogServlet extends BaseServlet {

    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String bizType = param(req, "bizType");
            String action = param(req, "logAction");
            String keyword = param(req, "keyword");
            int page = intParam(req, "page", 1);
            String base = req.getContextPath() + "/admin/audit-logs";
            String url = WebUtil.url(base, WebUtil.params(
                    "bizType", bizType, "logAction", action, "keyword", keyword));
            PageResult<AuditLog> pageData = auditLogService.page(bizType, action, keyword, page, 15, url);
            req.setAttribute("pageData", pageData);
            req.setAttribute("bizType", bizType);
            req.setAttribute("logAction", action);
            req.setAttribute("keyword", keyword);
            req.setAttribute("pageTitle", "审核日志");
            req.setAttribute("pageSubtitle", "所有商品审核、举报处理、订单流转和用户处罚记录");
            req.setAttribute("activeMenu", "logs");
            forward(req, resp, "audit/list.jsp");
        } catch (Exception ex) {
            throw new ServletException("加载审核日志失败", ex);
        }
    }
}
