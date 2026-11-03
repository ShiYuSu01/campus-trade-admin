package com.campus.trade.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.campus.trade.common.BizException;
import com.campus.trade.entity.User;

/**
 * Servlet 基类：封装转发、重定向、参数解析、Flash 提示和统一异常处理。
 */
public abstract class BaseServlet extends HttpServlet {

    protected final Logger log = Logger.getLogger(getClass().getName());

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected void flash(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    protected void flashSuccess(HttpServletRequest req, String message) {
        flash(req, "success", message);
    }

    protected void flashError(HttpServletRequest req, String message) {
        flash(req, "error", message);
    }

    protected String param(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    protected String param(HttpServletRequest req, String name, String defaultValue) {
        String value = param(req, name);
        return value.isEmpty() ? defaultValue : value;
    }

    protected int intParam(HttpServletRequest req, String name, int defaultValue) {
        try {
            return Integer.parseInt(param(req, name));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    protected long longParam(HttpServletRequest req, String name, long defaultValue) {
        try {
            return Long.parseLong(param(req, name));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    protected long requireId(HttpServletRequest req) {
        long id = longParam(req, "id", -1);
        if (id <= 0) {
            throw new BizException("参数错误：缺少有效的 ID");
        }
        return id;
    }

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("loginUser");
    }

    protected void handleError(HttpServletRequest req, HttpServletResponse resp,
                               Exception ex, String redirectPath) throws IOException {
        if (ex instanceof BizException) {
            flashError(req, ex.getMessage());
        } else {
            log.log(Level.SEVERE, "后台操作失败", ex);
            flashError(req, "操作失败，请稍后重试或查看 Tomcat 日志");
        }
        redirect(req, resp, redirectPath);
    }

    protected void handleError(HttpServletRequest req, HttpServletResponse resp,
                               Exception ex, String redirectPath, String fallbackMessage) throws IOException {
        if (ex instanceof BizException) {
            flashError(req, ex.getMessage());
        } else {
            log.log(Level.SEVERE, fallbackMessage, ex);
            flashError(req, fallbackMessage);
        }
        redirect(req, resp, redirectPath);
    }
}
