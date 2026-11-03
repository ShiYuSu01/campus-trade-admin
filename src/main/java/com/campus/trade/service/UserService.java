package com.campus.trade.service;

import java.util.List;

import com.campus.trade.common.BizException;
import com.campus.trade.common.DbUtil;
import com.campus.trade.common.PageResult;
import com.campus.trade.common.PasswordUtil;
import com.campus.trade.dao.AuditLogDao;
import com.campus.trade.dao.OrderDao;
import com.campus.trade.dao.ProductDao;
import com.campus.trade.dao.ReportDao;
import com.campus.trade.dao.UserDao;
import com.campus.trade.dto.UserDetail;
import com.campus.trade.entity.AuditLog;
import com.campus.trade.entity.User;

/**
 * 用户管理服务。
 */
public class UserService {

    private final UserDao userDao = new UserDao();
    private final ProductDao productDao = new ProductDao();
    private final OrderDao orderDao = new OrderDao();
    private final ReportDao reportDao = new ReportDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public PageResult<User> page(String keyword, String role, String status,
                                 int page, int size, String url) throws Exception {
        int safeSize = Math.max(1, Math.min(size, 50));
        long total = userDao.count(keyword, role, status);
        int pages = total == 0 ? 0 : (int) ((total + safeSize - 1) / safeSize);
        int safePage = Math.max(1, pages == 0 ? 1 : Math.min(page, pages));
        int offset = (safePage - 1) * safeSize;
        List<User> items = userDao.findPage(keyword, role, status, offset, safeSize);
        return PageResult.of(items, safePage, safeSize, total, url);
    }

    public User findById(long id) throws Exception {
        User user = userDao.findDetailById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    public UserDetail detail(long id) throws Exception {
        UserDetail detail = new UserDetail();
        detail.setUser(findById(id));
        detail.setProducts(productDao.findBySeller(id, 8));
        detail.setOrders(orderDao.findByBuyer(id, 8));
        detail.setReports(reportDao.findByReporter(id, 8));
        return detail;
    }

    public void changeStatus(long userId, String targetStatus, String reason, User operator)
            throws Exception {
        if (!"ACTIVE".equals(targetStatus) && !"BANNED".equals(targetStatus)) {
            throw new BizException("不支持的用户状态");
        }
        if (operator.getId().equals(userId)) {
            throw new BizException("不能修改自己的账号状态");
        }
        User user = findById(userId);
        if ("ADMIN".equals(user.getRole())) {
            throw new BizException("不能封禁管理员账号");
        }
        if ("BANNED".equals(targetStatus) && (reason == null || reason.trim().length() < 2)) {
            throw new BizException("封禁用户时必须填写原因");
        }
        String note = reason == null || reason.isBlank()
                ? ("BANNED".equals(targetStatus) ? "管理员封禁" : "管理员解封")
                : reason.trim();
        DbUtil.inTransaction(connection -> {
            userDao.updateStatus(connection, userId, targetStatus);
            auditLogDao.insert(connection, buildLog("USER", userId,
                    "BANNED".equals(targetStatus) ? "BAN" : "UNBAN",
                    operator, user.getStatus(), targetStatus, note));
            return null;
        });
    }

    public void resetPassword(long userId, User operator) throws Exception {
        User user = findById(userId);
        if ("ADMIN".equals(user.getRole())) {
            throw new BizException("不能在这里重置管理员密码");
        }
        String encoded = PasswordUtil.hash("123456");
        DbUtil.inTransaction(connection -> {
            userDao.updatePassword(connection, userId, encoded);
            auditLogDao.insert(connection, buildLog("USER", userId, "RESET_PASSWORD",
                    operator, user.getStatus(), user.getStatus(), "管理员重置密码为默认密码"));
            return null;
        });
    }

    public long countUsers() throws Exception {
        return userDao.countUsers();
    }

    public long countToday() throws Exception {
        return userDao.countToday();
    }

    public long countBanned() throws Exception {
        return userDao.countBanned();
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
