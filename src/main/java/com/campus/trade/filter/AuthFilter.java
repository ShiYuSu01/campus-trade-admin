package com.campus.trade.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.campus.trade.entity.User;

/**
 * 后台登录拦截：未登录跳转登录页，非管理员拒绝访问。
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            String target = req.getRequestURI();
            if (req.getQueryString() != null) {
                target += "?" + req.getQueryString();
            }
            String redirect = URLEncoder.encode(target, StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + redirect);
            return;
        }

        if (!"ADMIN".equals(loginUser.getRole()) || !"ACTIVE".equals(loginUser.getStatus())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(req, resp);
            return;
        }

        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        resp.setHeader("Pragma", "no-cache");
        chain.doFilter(request, response);
    }
}
