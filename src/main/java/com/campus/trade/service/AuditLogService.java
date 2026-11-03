package com.campus.trade.service;

import java.util.List;

import com.campus.trade.common.PageResult;
import com.campus.trade.dao.AuditLogDao;
import com.campus.trade.entity.AuditLog;

/**
 * 审核日志查询服务。
 */
public class AuditLogService {

    private final AuditLogDao auditLogDao = new AuditLogDao();

    public PageResult<AuditLog> page(String bizType, String action, String keyword,
                                     int page, int size, String url) throws Exception {
        int safeSize = Math.max(1, Math.min(size, 50));
        long total = auditLogDao.count(bizType, action, keyword);
        int pages = total == 0 ? 0 : (int) ((total + safeSize - 1) / safeSize);
        int safePage = Math.max(1, pages == 0 ? 1 : Math.min(page, pages));
        int offset = (safePage - 1) * safeSize;
        List<AuditLog> items = auditLogDao.findPage(bizType, action, keyword, offset, safeSize);
        return PageResult.of(items, safePage, safeSize, total, url);
    }

    public List<AuditLog> findByBiz(String bizType, long bizId) throws Exception {
        return auditLogDao.findByBiz(bizType, bizId, 30);
    }
}
