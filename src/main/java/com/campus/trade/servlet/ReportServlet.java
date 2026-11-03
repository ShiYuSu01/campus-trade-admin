package com.campus.trade.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campus.trade.common.ExcelUtil;
import com.campus.trade.common.PageResult;
import com.campus.trade.common.WebUtil;
import com.campus.trade.entity.Report;
import com.campus.trade.entity.User;
import com.campus.trade.service.AuditLogService;
import com.campus.trade.service.ReportService;
import com.campus.trade.web.BaseServlet;

/**
 * 举报处理：列表、详情、受理、处理、Excel 导出。
 */
@WebServlet("/admin/reports")
public class ReportServlet extends BaseServlet {

    private final ReportService reportService = new ReportService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action", "list");
        try {
            if ("detail".equals(action)) {
                showDetail(req, resp);
            } else if ("export".equals(action)) {
                export(req, resp);
            } else {
                showList(req, resp);
            }
        } catch (Exception ex) {
            if ("export".equals(action)) {
                log("导出举报失败", ex);
                flashError(req, "导出失败：" + ex.getMessage());
                redirect(req, resp, "/admin/reports");
            } else {
                throw new ServletException("加载举报页面失败", ex);
            }
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = param(req, "keyword");
        String status = param(req, "status");
        String targetType = param(req, "targetType");
        int page = intParam(req, "page", 1);
        String base = req.getContextPath() + "/admin/reports";
        String url = WebUtil.url(base, WebUtil.params(
                "action", "list", "keyword", keyword, "status", status, "targetType", targetType));
        PageResult<Report> pageData = reportService.page(keyword, status, targetType, page, 10, url);
        req.setAttribute("pageData", pageData);
        req.setAttribute("keyword", keyword);
        req.setAttribute("status", status);
        req.setAttribute("targetType", targetType);
        req.setAttribute("pageTitle", "举报处理");
        req.setAttribute("pageSubtitle", "核实用户举报，联动下架商品、封禁用户或扣减信用分");
        req.setAttribute("activeMenu", "reports");
        forward(req, resp, "report/list.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = requireId(req);
        Report report = reportService.findById(id);
        req.setAttribute("report", report);
        req.setAttribute("targetInfo", reportService.targetInfo(report));
        req.setAttribute("logs", auditLogService.findByBiz("REPORT", id));
        req.setAttribute("pageTitle", "举报详情");
        req.setAttribute("pageSubtitle", "举报编号 #" + id);
        req.setAttribute("activeMenu", "reports");
        forward(req, resp, "report/detail.jsp");
    }

    private void export(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = param(req, "keyword");
        String status = param(req, "status");
        String targetType = param(req, "targetType");
        List<Report> reports = reportService.export(keyword, status, targetType);
        ExcelUtil.writeReports(resp, reports);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action");
        long id = longParam(req, "id", 0);
        User operator = currentUser(req);
        if ("process".equals(action)) {
            try {
                reportService.markProcessing(id, param(req, "result"), operator);
                flashSuccess(req, "举报已标记为处理中");
                redirect(req, resp, "/admin/reports?action=detail&id=" + id);
            } catch (Exception ex) {
                handleError(req, resp, ex,
                        id > 0 ? "/admin/reports?action=detail&id=" + id : "/admin/reports");
            }
            return;
        }
        if (!"handle".equals(action)) {
            redirect(req, resp, "/admin/reports");
            return;
        }
        String decision = param(req, "decision");
        String handleAction = param(req, "handleAction");
        String result = param(req, "result");
        try {
            reportService.handle(id, decision, handleAction, result, operator);
            flashSuccess(req, "举报处理完成");
            redirect(req, resp, "/admin/reports?action=detail&id=" + id);
        } catch (Exception ex) {
            handleError(req, resp, ex,
                    id > 0 ? "/admin/reports?action=detail&id=" + id : "/admin/reports");
        }
    }
}
