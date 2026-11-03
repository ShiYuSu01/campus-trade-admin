package com.campus.trade.service;

import java.sql.Connection;
import java.util.List;

import com.campus.trade.common.BizException;
import com.campus.trade.common.DbUtil;
import com.campus.trade.common.PageResult;
import com.campus.trade.dao.AuditLogDao;
import com.campus.trade.dao.OrderDao;
import com.campus.trade.dao.ProductDao;
import com.campus.trade.dao.ReportDao;
import com.campus.trade.dao.UserDao;
import com.campus.trade.dto.TargetInfo;
import com.campus.trade.entity.AuditLog;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.Report;
import com.campus.trade.entity.User;

/**
 * 举报处理服务：处理举报的同时联动下架商品、封禁用户或扣减信用分，并写审核日志。
 */
public class ReportService {

    private final ReportDao reportDao = new ReportDao();
    private final ProductDao productDao = new ProductDao();
    private final OrderDao orderDao = new OrderDao();
    private final UserDao userDao = new UserDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public PageResult<Report> page(String keyword, String status, String targetType,
                                   int page, int size, String url) throws Exception {
        int safeSize = Math.max(1, Math.min(size, 50));
        long total = reportDao.count(keyword, status, targetType);
        int pages = total == 0 ? 0 : (int) ((total + safeSize - 1) / safeSize);
        int safePage = Math.max(1, pages == 0 ? 1 : Math.min(page, pages));
        int offset = (safePage - 1) * safeSize;
        List<Report> items = reportDao.findPage(keyword, status, targetType, offset, safeSize);
        return PageResult.of(items, safePage, safeSize, total, url);
    }

    public Report findById(long id) throws Exception {
        Report report = reportDao.findById(id);
        if (report == null) {
            throw new BizException("举报记录不存在");
        }
        return report;
    }

    public TargetInfo targetInfo(Report report) throws Exception {
        TargetInfo info = new TargetInfo();
        info.setType(report.getTargetTypeLabel());
        info.setId(report.getTargetId());
        switch (report.getTargetType()) {
            case "PRODUCT" -> {
                Product product = productDao.findById(report.getTargetId());
                if (product == null) {
                    info.setExists(false);
                    info.setTitle("商品已删除");
                    info.setSubtitle("无法查看被举报商品");
                    return info;
                }
                info.setExists(true);
                info.setTitle(product.getTitle());
                info.setSubtitle("卖家：" + product.getSellerName() + " · " + product.getPriceText()
                        + " · " + product.getCategoryName());
                info.setStatus(product.getStatus());
                info.setStatusLabel(product.getStatusLabel());
                info.setStatusClass(product.getStatusClass());
                info.setLink("/admin/products?action=detail&id=" + product.getId());
                info.setDescription(product.getShortDescription());
            }
            case "ORDER" -> {
                Order order = orderDao.findById(report.getTargetId());
                if (order == null) {
                    info.setExists(false);
                    info.setTitle("订单不存在");
                    return info;
                }
                info.setExists(true);
                info.setTitle("订单 " + order.getOrderNo());
                info.setSubtitle(order.getProductTitle() + " · " + order.getAmountText()
                        + " · 买家：" + order.getBuyerName());
                info.setStatus(order.getStatus());
                info.setStatusLabel(order.getStatusLabel());
                info.setStatusClass(order.getStatusClass());
                info.setLink("/admin/orders?action=detail&id=" + order.getId());
                info.setDescription("卖家：" + order.getSellerName() + "，下单时间：" + order.getCreateTimeText());
            }
            case "USER" -> {
                User user = userDao.findDetailById(report.getTargetId());
                if (user == null) {
                    info.setExists(false);
                    info.setTitle("用户不存在");
                    return info;
                }
                info.setExists(true);
                info.setTitle(user.getNickname());
                info.setSubtitle("@" + user.getUsername() + " · " + user.getCampus()
                        + " · 信用分 " + user.getCreditScore());
                info.setStatus(user.getStatus());
                info.setStatusLabel(user.getStatusLabel());
                info.setStatusClass(user.getStatusClass());
                info.setLink("/admin/users?action=detail&id=" + user.getId());
                info.setDescription("注册时间：" + user.getCreateTimeText()
                        + "，发布商品 " + user.getProductCount() + " 件");
            }
            default -> {
                info.setExists(false);
                info.setTitle("未知对象");
            }
        }
        return info;
    }

    /**
     * 处理举报。decision = RESOLVED / REJECTED；action = NONE / OFF_SHELF / BAN_USER / WARN。
     */
    public void handle(long reportId, String decision, String action, String result, User operator)
            throws Exception {
        if (!"RESOLVED".equals(decision) && !"REJECTED".equals(decision)) {
            throw new BizException("请选择处理结论");
        }
        if (result == null || result.trim().length() < 2) {
            throw new BizException("请填写处理说明（至少 2 个字）");
        }
        Report report = findById(reportId);
        if (!report.isPending() && !report.isProcessing()) {
            throw new BizException("该举报已经处理完成，不能重复处理");
        }
        String finalResult = result.trim();
        if (finalResult.length() > 500) {
            finalResult = finalResult.substring(0, 500);
        }
        String finalAction = "REJECTED".equals(decision) ? "NONE"
                : (action == null || action.isBlank() ? "NONE" : action);
        if (!List.of("NONE", "OFF_SHELF", "BAN_USER", "WARN").contains(finalAction)) {
            throw new BizException("处罚动作不正确");
        }

        Long targetProductId = null;
        Long targetUserId = null;
        if ("OFF_SHELF".equals(finalAction)) {
            targetProductId = resolveProductId(report);
            if (targetProductId == null) {
                throw new BizException("该举报对象不是商品，无法执行商品下架");
            }
        }
        if ("BAN_USER".equals(finalAction) || "WARN".equals(finalAction)) {
            targetUserId = resolveUserId(report);
            if (targetUserId == null) {
                throw new BizException("无法确定要处罚的用户");
            }
            User target = userDao.findById(targetUserId);
            if (target == null) {
                throw new BizException("被处罚用户不存在");
            }
            if ("ADMIN".equals(target.getRole())) {
                throw new BizException("不能处罚管理员账号");
            }
        }

        final String actionValue = finalAction;
        final String decisionValue = decision;
        final String resultValue = finalResult;
        final Long productId = targetProductId;
        final Long userId = targetUserId;
        DbUtil.inTransaction(connection -> {
            reportDao.handle(connection, reportId, decisionValue, operator.getId(), resultValue, actionValue);
            auditLogDao.insert(connection, buildLog("REPORT", reportId, "HANDLE",
                    operator, report.getStatus(), decisionValue, resultValue));

            if ("OFF_SHELF".equals(actionValue) && productId != null) {
                Product product = productDao.findById(productId);
                productDao.updateStatus(connection, productId, "OFF_SHELF",
                        "举报处理下架：" + resultValue);
                auditLogDao.insert(connection, buildLog("PRODUCT", productId, "OFF_SHELF",
                        operator, product == null ? null : product.getStatus(), "OFF_SHELF",
                        "举报 #" + reportId + " 处理联动下架"));
            }
            if ("BAN_USER".equals(actionValue) && userId != null) {
                User target = userDao.findById(userId);
                userDao.updateStatus(connection, userId, "BANNED");
                auditLogDao.insert(connection, buildLog("USER", userId, "BAN",
                        operator, target == null ? null : target.getStatus(), "BANNED",
                        "举报 #" + reportId + " 处理联动封禁"));
            }
            if ("WARN".equals(actionValue) && userId != null) {
                User target = userDao.findById(userId);
                userDao.updateCredit(connection, userId, -5);
                auditLogDao.insert(connection, buildLog("USER", userId, "WARN",
                        operator, target == null ? null : target.getStatus(),
                        target == null ? null : target.getStatus(),
                        "举报 #" + reportId + " 处理：信用分扣减 5 分"));
            }
            return null;
        });
    }

    public void markProcessing(long reportId, String result, User operator) throws Exception {
        Report report = findById(reportId);
        if (!report.isPending()) {
            throw new BizException("只有待处理的举报可以标记为处理中");
        }
        String note = result == null || result.isBlank() ? "管理员已受理，开始核实" : result.trim();
        DbUtil.inTransaction(connection -> {
            reportDao.markProcessing(connection, reportId, operator.getId(), note);
            auditLogDao.insert(connection, buildLog("REPORT", reportId, "PROCESS",
                    operator, "PENDING", "PROCESSING", note));
            return null;
        });
    }

    public List<Report> export(String keyword, String status, String targetType) throws Exception {
        return reportDao.findPage(keyword, status, targetType, 0, 5000);
    }

    public long countPending() throws Exception {
        return reportDao.countPending();
    }

    public long countAll() throws Exception {
        return reportDao.countAll();
    }

    public long countResolved() throws Exception {
        return reportDao.countResolved();
    }

    public List<Report> pending(int limit) throws Exception {
        return reportDao.findPending(limit);
    }

    private Long resolveProductId(Report report) throws Exception {
        return switch (report.getTargetType()) {
            case "PRODUCT" -> report.getTargetId();
            case "ORDER" -> {
                Order order = orderDao.findById(report.getTargetId());
                yield order == null ? null : order.getProductId();
            }
            default -> null;
        };
    }

    private Long resolveUserId(Report report) throws Exception {
        return switch (report.getTargetType()) {
            case "USER" -> report.getTargetId();
            case "PRODUCT" -> {
                Product product = productDao.findById(report.getTargetId());
                yield product == null ? null : product.getSellerId();
            }
            case "ORDER" -> {
                Order order = orderDao.findById(report.getTargetId());
                yield order == null ? null : order.getSellerId();
            }
            default -> null;
        };
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
