package com.campus.trade.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campus.trade.entity.Category;
import com.campus.trade.entity.User;
import com.campus.trade.service.CategoryService;
import com.campus.trade.web.BaseServlet;

/**
 * 分类管理：列表、新增、编辑、启停、删除。
 */
@WebServlet("/admin/categories")
public class CategoryServlet extends BaseServlet {

    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Category> categories = categoryService.findAll();
            req.setAttribute("categories", categories);
            if ("edit".equals(param(req, "action"))) {
                long id = longParam(req, "id", 0);
                if (id > 0) {
                    req.setAttribute("editCategory", categoryService.findById(id));
                }
            }
            req.setAttribute("pageTitle", "分类管理");
            req.setAttribute("pageSubtitle", "维护商品分类，控制前台可选的分类范围");
            req.setAttribute("activeMenu", "categories");
            forward(req, resp, "category/list.jsp");
        } catch (Exception ex) {
            throw new ServletException("加载分类页面失败", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action", "save");
        User operator = currentUser(req);
        try {
            switch (action) {
                case "save" -> {
                    long id = longParam(req, "id", 0);
                    categoryService.save(id > 0 ? id : null, param(req, "name"),
                            param(req, "icon"), intParam(req, "sort", 0), operator);
                    flashSuccess(req, "分类已保存");
                }
                case "toggle" -> {
                    categoryService.toggle(longParam(req, "id", 0), operator);
                    flashSuccess(req, "分类状态已切换");
                }
                case "delete" -> {
                    categoryService.delete(longParam(req, "id", 0), operator);
                    flashSuccess(req, "分类已删除");
                }
                default -> {
                    // 未知操作直接回列表
                }
            }
            redirect(req, resp, "/admin/categories");
        } catch (Exception ex) {
            handleError(req, resp, ex, "/admin/categories");
        }
    }
}
