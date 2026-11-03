package com.campus.trade.service;

import java.util.ArrayList;
import java.util.List;

import com.campus.trade.common.BizException;
import com.campus.trade.common.DbUtil;
import com.campus.trade.common.PageResult;
import com.campus.trade.dao.AuditLogDao;
import com.campus.trade.dao.OrderDao;
import com.campus.trade.dto.StatusOption;
import com.campus.trade.entity.AuditLog;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.User;

/**
 * 订单查询与状态流转服务。
 */
public class OrderService {

    private final OrderDao orderDao = new OrderDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public PageResult<Order> page(String keyword, String status, int page, int size, String url)
            throws Exception {
        int safeSize = Math.max(1, Math.min(size, 50));
        long total = orderDao.count(keyword, status);
        int pages = total == 0 ? 0 : (int) ((total + safeSize - 1) / safeSize);
        int safePage = Math.max(1, pages == 0 ? 1 : Math.min(page, pages));
        int offset = (safePage - 1) * safeSize;
        List<Order> items = orderDao.findPage(keyword, status, offset, safeSize);
        return PageResult.of(items, safePage, safeSize, total, url);
    }

    public Order findById(long id) throws Exception {
        Order order = orderDao.findById(id);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    public List<StatusOption> nextStatuses(Order order) {
        List<StatusOption> options = new ArrayList<>();
        if (order == null || order.getStatus() == null) {
            return options;
        }
        switch (order.getStatus()) {
            case "PENDING_PAYMENT" -> {
                options.add(new StatusOption("PAID", "标记为已付款"));
                options.add(new StatusOption("CANCELLED", "取消订单"));
            }
            case "PAID" -> {
                options.add(new StatusOption("SHIPPED", "确认发货 / 已交付"));
                options.add(new StatusOption("REFUNDING", "转入退款处理"));
                options.add(new StatusOption("CANCELLED", "取消订单"));
            }
            case "SHIPPED" -> {
                options.add(new StatusOption("COMPLETED", "确认交易完成"));
                options.add(new StatusOption("REFUNDING", "转入退款处理"));
            }
            case "REFUNDING" -> {
                options.add(new StatusOption("REFUNDED", "确认已退款"));
                options.add(new StatusOption("PAID", "驳回退款，恢复待发货"));
            }
            case "COMPLETED" -> options.add(new StatusOption("REFUNDING", "发起售后 / 退款"));
            default -> {
                // 已取消、已退款为终态
            }
        }
        return options;
    }

    public void updateStatus(long orderId, String targetStatus, String remark, User operator)
            throws Exception {
        Order order = findById(orderId);
        boolean allowed = nextStatuses(order).stream().anyMatch(option -> option.getValue().equals(targetStatus));
        if (!allowed) {
            throw new BizException("当前状态为「" + order.getStatusLabel() + "」，不能流转到该状态");
        }
        String action = switch (targetStatus) {
            case "PAID" -> "PAY";
            case "SHIPPED" -> "SHIP";
            case "COMPLETED" -> "COMPLETE";
            case "CANCELLED" -> "CANCEL";
            case "REFUNDING" -> "REFUNDING";
            case "REFUNDED" -> "REFUND";
            default -> "UPDATE";
        };
        String note = remark == null || remark.isBlank() ? "管理员流转订单状态" : remark.trim();
        DbUtil.inTransaction(connection -> {
            orderDao.updateStatus(connection, orderId, targetStatus);
            auditLogDao.insert(connection, buildLog("ORDER", orderId, action,
                    operator, order.getStatus(), targetStatus, note));
            return null;
        });
    }

    public List<Order> export(String keyword, String status) throws Exception {
        return orderDao.findPage(keyword, status, 0, 5000);
    }

    public long countAll() throws Exception {
        return orderDao.countAll();
    }

    public long countToday() throws Exception {
        return orderDao.countToday();
    }

    public List<Order> recent(int limit) throws Exception {
        return orderDao.findRecent(limit);
    }

    public List<Order> findByUser(long userId, int limit) throws Exception {
        List<Order> orders = new ArrayList<>(orderDao.findByBuyer(userId, limit));
        for (Order order : orderDao.findBySeller(userId, limit)) {
            if (orders.stream().noneMatch(item -> item.getId().equals(order.getId()))) {
                orders.add(order);
            }
        }
        orders.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return orders.size() > limit ? orders.subList(0, limit) : orders;
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
