package com.campus.trade.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.campus.trade.common.DbUtil;
import com.campus.trade.entity.Report;

/**
 * 举报数据访问。
 */
public class ReportDao {

    private final QueryRunner qr = new QueryRunner(DbUtil.getDataSource());

    private static final String BASE_SELECT = """
            SELECT r.id, r.reporter_id AS reporterId, r.target_type AS targetType, r.target_id AS targetId,
                   r.reason_type AS reasonType, r.description, r.evidence_images AS evidenceImages,
                   r.status, r.handler_id AS handlerId, r.handle_result AS handleResult,
                   r.handle_action AS handleAction, r.handled_at AS handledAt,
                   r.create_time AS createTime, r.update_time AS updateTime,
                   reporter.nickname AS reporterName, handler.nickname AS handlerName,
                   CASE r.target_type
                       WHEN 'PRODUCT' THEN (SELECT title FROM t_product WHERE id = r.target_id)
                       WHEN 'ORDER' THEN (SELECT CONCAT('订单 ', order_no) FROM t_order WHERE id = r.target_id)
                       WHEN 'USER' THEN (SELECT nickname FROM t_user WHERE id = r.target_id)
                       ELSE NULL
                   END AS targetTitle
            FROM t_report r
            JOIN t_user reporter ON r.reporter_id = reporter.id
            LEFT JOIN t_user handler ON r.handler_id = handler.id
            """;

    public Report findById(long id) throws Exception {
        return qr.query(BASE_SELECT + " WHERE r.id = ?", new BeanHandler<>(Report.class), id);
    }

    public List<Report> findPage(String keyword, String status, String targetType, int offset, int limit)
            throws Exception {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status, targetType);
        sql.append(" ORDER BY r.create_time DESC, r.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return qr.query(sql.toString(), new BeanListHandler<>(Report.class), params.toArray());
    }

    public long count(String keyword, String status, String targetType) throws Exception {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM t_report r
                JOIN t_user reporter ON r.reporter_id = reporter.id
                WHERE 1 = 1
                """);
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status, targetType);
        Number number = qr.query(sql.toString(), new ScalarHandler<>(), params.toArray());
        return number == null ? 0 : number.longValue();
    }

    private void appendFilters(StringBuilder sql, List<Object> params,
                               String keyword, String status, String targetType) {
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (r.description LIKE ? OR reporter.nickname LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND r.status = ?");
            params.add(status);
        }
        if (targetType != null && !targetType.isBlank()) {
            sql.append(" AND r.target_type = ?");
            params.add(targetType);
        }
    }

    public int handle(Connection connection, long id, String status, long handlerId,
                      String result, String action) throws Exception {
        String sql = """
                UPDATE t_report
                SET status = ?, handler_id = ?, handle_result = ?, handle_action = ?,
                    handled_at = NOW(), update_time = NOW()
                WHERE id = ?
                """;
        return qr.update(connection, sql, status, handlerId, result, action, id);
    }

    public int markProcessing(Connection connection, long id, long handlerId, String result) throws Exception {
        String sql = """
                UPDATE t_report
                SET status = 'PROCESSING', handler_id = ?, handle_result = ?, update_time = NOW()
                WHERE id = ?
                """;
        return qr.update(connection, sql, handlerId, result, id);
    }

    public long countAll() throws Exception {
        return count(null, null, null);
    }

    public long countByStatus(String status) throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_report WHERE status = ?",
                new ScalarHandler<>(), status);
        return number == null ? 0 : number.longValue();
    }

    public long countPending() throws Exception {
        return countByStatus("PENDING");
    }

    public long countResolved() throws Exception {
        return countByStatus("RESOLVED");
    }

    public List<Report> findPending(int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE r.status = 'PENDING' ORDER BY r.create_time ASC LIMIT ?",
                new BeanListHandler<>(Report.class), limit);
    }

    public List<Report> findByReporter(long reporterId, int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE r.reporter_id = ? ORDER BY r.create_time DESC LIMIT ?",
                new BeanListHandler<>(Report.class), reporterId, limit);
    }

    public List<Report> findByTarget(String targetType, long targetId, int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE r.target_type = ? AND r.target_id = ? "
                        + "ORDER BY r.create_time DESC LIMIT ?",
                new BeanListHandler<>(Report.class), targetType, targetId, limit);
    }
}
