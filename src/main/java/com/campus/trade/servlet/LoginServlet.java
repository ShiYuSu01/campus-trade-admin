package com.campus.trade.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.campus.trade.common.BizException;
import com.campus.trade.entity.User;
import com.campus.trade.service.AuthService;
import com.campus.trade.web.BaseServlet;

/**
 * 后台登录。
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User current = currentUser(req);
        if (current != null && "ADMIN".equals(current.getRole())) {
            redirect(req, resp, "/admin/dashboard");
            return;
        }
        req.setAttribute("redirect", req.getParameter("redirect"));
        forward(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = param(req, "username");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");
        try {
            User user = authService.loginAdmin(username, password);
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("loginUser", user);

            if (redirect != null && redirect.startsWith(req.getContextPath() + "/admin/")) {
                resp.sendRedirect(redirect);
            } else {
                redirect(req, resp, "/admin/dashboard");
            }
        } catch (BizException ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("username", username);
            req.setAttribute("redirect", redirect);
            forward(req, resp, "login.jsp");
        } catch (Exception ex) {
            log("登录失败", ex);
            req.setAttribute("error", "登录失败，请检查数据库服务是否正常");
            req.setAttribute("username", username);
            req.setAttribute("redirect", redirect);
            forward(req, resp, "login.jsp");
        }
    }
}
