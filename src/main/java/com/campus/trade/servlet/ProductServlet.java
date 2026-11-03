package com.campus.trade.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.campus.trade.common.BizException;
import com.campus.trade.common.PageResult;
import com.campus.trade.common.WebUtil;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.User;
import com.campus.trade.service.ProductService;
import com.campus.trade.web.BaseServlet;

/**
 * 商品管理：列表、详情、审核、上下架、批量审核、删除。
 */
@WebServlet("/admin/products")
public class ProductServlet extends BaseServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action", "list");
        try {
            switch (action) {
                case "detail" -> showDetail(req, resp);
                default -> showList(req, resp);
            }
        } catch (BizException ex) {
            flashError(req, ex.getMessage());
            redirect(req, resp, "/admin/products");
        } catch (Exception ex) {
            throw new ServletException("加载商品页面失败", ex);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String keyword = param(req, "keyword");
        String status = param(req, "status");
        long categoryIdValue = longParam(req, "categoryId", 0);
        Long categoryId = categoryIdValue > 0 ? categoryIdValue : null;
        int page = intParam(req, "page", 1);
        String base = req.getContextPath() + "/admin/products";
        String url = WebUtil.url(base, WebUtil.params(
                "action", "list",
                "keyword", keyword,
                "status", status,
                "categoryId", categoryId == null ? "" : String.valueOf(categoryId)));
        PageResult<Product> pageData = productService.page(keyword, status, categoryId, page, 8, url);
        req.setAttribute("pageData", pageData);
        req.setAttribute("categories", productService.categories());
        req.setAttribute("keyword", keyword);
        req.setAttribute("status", status);
        req.setAttribute("categoryId", categoryId);
        req.setAttribute("pageTitle", "商品审核");
        req.setAttribute("pageSubtitle", "审核卖家发布的二手商品，处理违规上架");
        req.setAttribute("activeMenu", "products");
        forward(req, resp, "product/list.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        long id = requireId(req);
        Product product = productService.findById(id);
        req.setAttribute("product", product);
        req.setAttribute("logs", productService.logs(id));
        req.setAttribute("categories", productService.categories());
        req.setAttribute("pageTitle", "商品详情");
        req.setAttribute("pageSubtitle", "商品信息、审核记录与操作");
        req.setAttribute("activeMenu", "products");
        forward(req, resp, "product/detail.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action");
        User operator = currentUser(req);
        switch (action) {
            case "audit" -> audit(req, resp, operator);
            case "batch" -> batch(req, resp, operator);
            case "status" -> changeStatus(req, resp, operator);
            case "delete" -> delete(req, resp, operator);
            default -> redirect(req, resp, "/admin/products");
        }
    }

    private void audit(HttpServletRequest req, HttpServletResponse resp, User operator)
            throws IOException {
        long id = longParam(req, "id", 0);
        String decision = param(req, "decision");
        String reason = param(req, "reason");
        try {
            productService.audit(id, decision, reason, operator);
            flashSuccess(req, "商品审核完成：" + ("APPROVED".equals(decision) ? "已通过" : "已驳回"));
            redirect(req, resp, "/admin/products?action=detail&id=" + id);
        } catch (Exception ex) {
            handleError(req, resp, ex,
                    id > 0 ? "/admin/products?action=detail&id=" + id : "/admin/products");
        }
    }

    private void batch(HttpServletRequest req, HttpServletResponse resp, User operator)
            throws IOException {
        String[] rawIds = req.getParameterValues("ids");
        List<Long> ids = new ArrayList<>();
        if (rawIds != null) {
            for (String raw : rawIds) {
                try {
                    ids.add(Long.parseLong(raw));
                } catch (NumberFormatException ignored) {
                    // 忽略非法 ID
                }
            }
        }
        try {
            int count = productService.batchApprove(ids, operator);
            flashSuccess(req, "已批量通过 " + count + " 件商品");
            redirect(req, resp, "/admin/products");
        } catch (Exception ex) {
            handleError(req, resp, ex, "/admin/products");
        }
    }

    private void changeStatus(HttpServletRequest req, HttpServletResponse resp, User operator)
            throws IOException {
        long id = longParam(req, "id", 0);
        String status = param(req, "status");
        String reason = param(req, "reason");
        try {
            productService.changeStatus(id, status, reason, operator);
            flashSuccess(req, "商品状态已更新为「" + ("OFF_SHELF".equals(status) ? "已下架" : "已通过") + "」");
            redirect(req, resp, "/admin/products?action=detail&id=" + id);
        } catch (Exception ex) {
            handleError(req, resp, ex,
                    id > 0 ? "/admin/products?action=detail&id=" + id : "/admin/products");
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp, User operator)
            throws IOException {
        long id = longParam(req, "id", 0);
        try {
            productService.delete(id, operator);
            flashSuccess(req, "商品已删除");
            redirect(req, resp, "/admin/products");
        } catch (Exception ex) {
            handleError(req, resp, ex,
                    id > 0 ? "/admin/products?action=detail&id=" + id : "/admin/products");
        }
    }
}
