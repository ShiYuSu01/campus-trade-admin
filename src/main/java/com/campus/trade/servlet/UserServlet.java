package com.campus.trade.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.WebUtil;
import com.campus.trade.dto.UserDetail;
import com.campus.trade.entity.User;
import com.campus.trade.service.AuditLogService;
import com.campus.trade.service.UserService;
import com.campus.trade.web.BaseServlet;

/**
 * 用户管理：列表、详情、封禁/解封、重置密码。
 */
@WebServlet("/admin/users")
public class UserServlet extends BaseServlet {

    private final UserService userService = new UserService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action", "list");
        try {
            if ("detail".equals(action)) {
                showDetail(req, resp);
            } else {
                showList(req, resp);
            }
        } catch (Exception ex) {
            throw new ServletException("加载用户页面失败", ex);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = param(req, "keyword");
        String role = param(req, "role");
        String status = param(req, "status");
        int page = intParam(req, "page", 1);
        String base = req.getContextPath() + "/admin/users";
        String url = WebUtil.url(base, WebUtil.params(
                "action", "list", "keyword", keyword, "role", role, "status", status));
        PageResult<User> pageData = userService.page(keyword, role, status, page, 10, url);
        req.setAttribute("pageData", pageData);
        req.setAttribute("keyword", keyword);
        req.setAttribute("role", role);
        req.setAttribute("status", status);
        req.setAttribute("pageTitle", "用户管理");
        req.setAttribute("pageSubtitle", "管理平台注册用户、信用分与账号状态");
        req.setAttribute("activeMenu", "users");
        forward(req, resp, "user/list.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = requireId(req);
        UserDetail detail = userService.detail(id);
        req.setAttribute("detail", detail);
        req.setAttribute("user", detail.getUser());
        req.setAttribute("logs", auditLogService.findByBiz("USER", id));
        req.setAttribute("pageTitle", "用户详情");
        req.setAttribute("pageSubtitle", "用户 " + detail.getUser().getNickname());
        req.setAttribute("activeMenu", "users");
        forward(req, resp, "user/detail.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action");
        long id = longParam(req, "id", 0);
        User operator = currentUser(req);
        if ("status".equals(action)) {
            try {
                userService.changeStatus(id, param(req, "status"), param(req, "reason"), operator);
                flashSuccess(req, "用户状态已更新");
                redirect(req, resp, "/admin/users?action=detail&id=" + id);
            } catch (Exception ex) {
                handleError(req, resp, ex,
                        id > 0 ? "/admin/users?action=detail&id=" + id : "/admin/users");
            }
            return;
        }
        if ("resetPassword".equals(action)) {
            try {
                userService.resetPassword(id, operator);
                flashSuccess(req, "密码已重置为 123456，请提醒用户尽快修改");
                redirect(req, resp, "/admin/users?action=detail&id=" + id);
            } catch (Exception ex) {
                handleError(req, resp, ex,
                        id > 0 ? "/admin/users?action=detail&id=" + id : "/admin/users");
            }
            return;
        }
        redirect(req, resp, "/admin/users");
    }
}
