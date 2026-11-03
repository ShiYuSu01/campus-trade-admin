package com.campus.trade.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.campus.trade.common.BizException;
import com.campus.trade.common.DbUtil;
import com.campus.trade.common.PageResult;
import com.campus.trade.dao.AuditLogDao;
import com.campus.trade.dao.CategoryDao;
import com.campus.trade.dao.ProductDao;
import com.campus.trade.dto.CategoryStat;
import com.campus.trade.entity.AuditLog;
import com.campus.trade.entity.Category;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.User;

/**
 * 商品审核与商品管理服务。
 */
public class ProductService {

    private final ProductDao productDao = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public PageResult<Product> page(String keyword, String status, Long categoryId,
                                    int page, int size, String url) throws Exception {
        int safeSize = Math.max(1, Math.min(size, 50));
        long total = productDao.count(keyword, status, categoryId);
        int pages = total == 0 ? 0 : (int) ((total + safeSize - 1) / safeSize);
        int safePage = Math.max(1, pages == 0 ? 1 : Math.min(page, pages));
        int offset = (safePage - 1) * safeSize;
        List<Product> items = productDao.findPage(keyword, status, categoryId, offset, safeSize);
        return PageResult.of(items, safePage, safeSize, total, url);
    }

    public Product findById(long id) throws Exception {
        Product product = productDao.findById(id);
        if (product == null) {
            throw new BizException("商品不存在或已被删除");
        }
        return product;
    }

    public List<Category> categories() throws Exception {
        return categoryDao.findAll();
    }

    public List<AuditLog> logs(long productId) throws Exception {
        return auditLogDao.findByBiz("PRODUCT", productId, 20);
    }

    /**
     * 审核通过 / 驳回，必须是待审核状态。
     */
    public void audit(long productId, String decision, String reason, User operator) throws Exception {
        if (!"APPROVED".equals(decision) && !"REJECTED".equals(decision)) {
            throw new BizException("审核结论不正确");
        }
        if ("REJECTED".equals(decision) && (reason == null || reason.trim().length() < 2)) {
            throw new BizException("驳回时必须填写驳回原因");
        }
        Product product = findById(productId);
        if (!product.isPending()) {
            throw new BizException("该商品当前状态为「" + product.getStatusLabel() + "」，不能重复审核");
        }
        String finalReason = "REJECTED".equals(decision) ? reason.trim() : null;
        DbUtil.inTransaction(connection -> {
            productDao.audit(connection, productId, decision,
                    finalReason, operator.getId());
            auditLogDao.insert(connection, buildLog("PRODUCT", productId,
                    "APPROVED".equals(decision) ? "APPROVE" : "REJECT",
                    operator, "PENDING", decision, finalReason));
            return null;
        });
    }

    /**
     * 批量通过，只处理待审核商品。
     */
    public int batchApprove(List<Long> ids, User operator) throws Exception {
        if (ids == null || ids.isEmpty()) {
            throw new BizException("请先勾选要审核的商品");
        }
        List<Long> distinct = ids.stream().filter(id -> id != null && id > 0).distinct().toList();
        if (distinct.isEmpty()) {
            throw new BizException("请先勾选要审核的商品");
        }
        if (distinct.size() > 50) {
            throw new BizException("一次最多批量审核 50 件商品");
        }
        int count = DbUtil.inTransaction(connection -> {
            int affected = 0;
            for (Long id : distinct) {
                Product product = productDao.findById(id);
                if (product == null || !product.isPending()) {
                    continue;
                }
                affected += productDao.audit(connection, id, "APPROVED", null, operator.getId());
                auditLogDao.insert(connection, buildLog("PRODUCT", id, "APPROVE",
                        operator, "PENDING", "APPROVED", "批量审核通过"));
            }
            return affected;
        });
        if (count == 0) {
            throw new BizException("勾选的商品都不是待审核状态");
        }
        return count;
    }

    /**
     * 管理员下架 / 重新上架。
     */
    public void changeStatus(long productId, String targetStatus, String reason, User operator)
            throws Exception {
        if (!"OFF_SHELF".equals(targetStatus) && !"APPROVED".equals(targetStatus)) {
            throw new BizException("不支持的目标状态");
        }
        Product product = findById(productId);
        if (product.isSold()) {
            throw new BizException("已售出的商品不能下架或重新上架");
        }
        if ("OFF_SHELF".equals(targetStatus) && !product.isApproved()) {
            throw new BizException("只有已通过审核的商品可以下架");
        }
        if ("APPROVED".equals(targetStatus) && !product.isOffShelf() && !product.isRejected()) {
            throw new BizException("只有已下架或已驳回的商品可以重新上架");
        }
        String finalReason = "OFF_SHELF".equals(targetStatus)
                ? (reason == null || reason.isBlank() ? "管理员下架" : reason.trim())
                : null;
        DbUtil.inTransaction(connection -> {
            productDao.updateStatus(connection, productId, targetStatus, finalReason);
            auditLogDao.insert(connection, buildLog("PRODUCT", productId,
                    "OFF_SHELF".equals(targetStatus) ? "OFF_SHELF" : "RESTORE",
                    operator, product.getStatus(), targetStatus, finalReason));
            return null;
        });
    }

    /**
     * 仅允许删除无订单的驳回 / 下架商品，保留交易数据。
     */
    public void delete(long productId, User operator) throws Exception {
        Product product = findById(productId);
        if (product.getOrderCountSafe() > 0) {
            throw new BizException("该商品已产生订单，不能删除，请使用下架");
        }
        if (!product.isRejected() && !product.isOffShelf()) {
            throw new BizException("只有已驳回或已下架的商品可以删除");
        }
        DbUtil.inTransaction(connection -> {
            productDao.delete(connection, productId);
            auditLogDao.insert(connection, buildLog("PRODUCT", productId, "DELETE",
                    operator, product.getStatus(), null, "删除商品：" + product.getTitle()));
            return null;
        });
    }

    public long countPending() throws Exception {
        return productDao.countByStatus("PENDING");
    }

    public long countAll() throws Exception {
        return productDao.countAll();
    }

    public List<Product> topPending(int limit) throws Exception {
        return productDao.findTopPending(limit);
    }

    public List<CategoryStat> categoryStats() throws Exception {
        return productDao.categoryStats();
    }

    private AuditLog buildLog(String bizType, long bizId, String action, User operator,
                              String before, String after, String remark) {
        AuditLog log = new AuditLog();
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setAction(action);
        log.setOperatorId(operator.getId());
        log.setOperatorName(operator.getNickname());
        log.setBeforeStatus(before);
        log.setAfterStatus(after);
        log.setRemark(remark);
        return log;
    }
}
