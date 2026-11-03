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
import com.campus.trade.entity.Order;
import com.campus.trade.entity.User;
import com.campus.trade.service.AuditLogService;
import com.campus.trade.service.OrderService;
import com.campus.trade.web.BaseServlet;

/**
 * 订单管理：查询、详情、状态流转、Excel 导出。
 */
@WebServlet("/admin/orders")
public class OrderServlet extends BaseServlet {

    private final OrderService orderService = new OrderService();
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
                log("导出订单失败", ex);
                flashError(req, "导出失败：" + ex.getMessage());
                redirect(req, resp, "/admin/orders");
            } else {
                throw new ServletException("加载订单页面失败", ex);
            }
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = param(req, "keyword");
        String status = param(req, "status");
        int page = intParam(req, "page", 1);
        String base = req.getContextPath() + "/admin/orders";
        String url = WebUtil.url(base, WebUtil.params(
                "action", "list", "keyword", keyword, "status", status));
        PageResult<Order> pageData = orderService.page(keyword, status, page, 10, url);
        req.setAttribute("pageData", pageData);
        req.setAttribute("keyword", keyword);
        req.setAttribute("status", status);
        req.setAttribute("pageTitle", "订单管理");
        req.setAttribute("pageSubtitle", "查看交易订单，处理发货、完成与退款");
        req.setAttribute("activeMenu", "orders");
        forward(req, resp, "order/list.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = requireId(req);
        Order order = orderService.findById(id);
        req.setAttribute("order", order);
        req.setAttribute("nextStatuses", orderService.nextStatuses(order));
        req.setAttribute("logs", auditLogService.findByBiz("ORDER", id));
        req.setAttribute("pageTitle", "订单详情");
        req.setAttribute("pageSubtitle", "订单编号 " + order.getOrderNo());
        req.setAttribute("activeMenu", "orders");
        forward(req, resp, "order/detail.jsp");
    }

    private void export(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = param(req, "keyword");
        String status = param(req, "status");
        List<Order> orders = orderService.export(keyword, status);
        ExcelUtil.writeOrders(resp, orders);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = longParam(req, "id", 0);
        String targetStatus = param(req, "targetStatus");
        String remark = param(req, "remark");
        User operator = currentUser(req);
        try {
            orderService.updateStatus(id, targetStatus, remark, operator);
            flashSuccess(req, "订单状态已更新");
            redirect(req, resp, "/admin/orders?action=detail&id=" + id);
        } catch (Exception ex) {
            handleError(req, resp, ex,
                    id > 0 ? "/admin/orders?action=detail&id=" + id : "/admin/orders");
        }
    }
}
