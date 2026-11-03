package com.campus.trade.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.campus.trade.common.DbUtil;
import com.campus.trade.entity.AuditLog;

/**
 * 审核日志数据访问。
 */
public class AuditLogDao {

    private final QueryRunner qr = new QueryRunner(DbUtil.getDataSource());

    private static final String BASE_SELECT = """
            SELECT id, biz_type AS bizType, biz_id AS bizId, action,
                   operator_id AS operatorId, operator_name AS operatorName,
                   before_status AS beforeStatus, after_status AS afterStatus,
                   remark, create_time AS createTime
            FROM t_audit_log
            """;

    public void insert(Connection connection, AuditLog log) throws Exception {
        String sql = """
                INSERT INTO t_audit_log
                (biz_type, biz_id, action, operator_id, operator_name,
                 before_status, after_status, remark, create_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
                """;
        qr.update(connection, sql, log.getBizType(), log.getBizId(), log.getAction(),
                log.getOperatorId(), log.getOperatorName(), log.getBeforeStatus(),
                log.getAfterStatus(), log.getRemark());
    }

    public List<AuditLog> findPage(String bizType, String action, String keyword, int offset, int limit)
            throws Exception {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, bizType, action, keyword);
        sql.append(" ORDER BY create_time DESC, id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return qr.query(sql.toString(), new BeanListHandler<>(AuditLog.class), params.toArray());
    }

    public long count(String bizType, String action, String keyword) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_audit_log WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, bizType, action, keyword);
        Number number = qr.query(sql.toString(), new ScalarHandler<>(), params.toArray());
        return number == null ? 0 : number.longValue();
    }

    private void appendFilters(StringBuilder sql, List<Object> params,
                               String bizType, String action, String keyword) {
        if (bizType != null && !bizType.isBlank()) {
            sql.append(" AND biz_type = ?");
            params.add(bizType);
        }
        if (action != null && !action.isBlank()) {
            sql.append(" AND action = ?");
            params.add(action);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (remark LIKE ? OR operator_name LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
        }
    }

    public List<AuditLog> findByBiz(String bizType, long bizId, int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE biz_type = ? AND biz_id = ? "
                        + "ORDER BY create_time DESC, id DESC LIMIT ?",
                new BeanListHandler<>(AuditLog.class), bizType, bizId, limit);
    }

    public List<AuditLog> findRecent(int limit) throws Exception {
        return qr.query(BASE_SELECT + " ORDER BY create_time DESC, id DESC LIMIT ?",
                new BeanListHandler<>(AuditLog.class), limit);
    }
}
