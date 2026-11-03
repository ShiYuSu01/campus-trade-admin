package com.campus.trade.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.campus.trade.common.DbUtil;
import com.campus.trade.entity.User;

/**
 * 用户数据访问。
 */
public class UserDao {

    private final QueryRunner qr = new QueryRunner(DbUtil.getDataSource());

    private static final String BASE_COLUMNS = """
            id, username, nickname, phone, email, avatar, role, status,
            credit_score AS creditScore, campus,
            last_login_time AS lastLoginTime, create_time AS createTime, update_time AS updateTime
            """;

    public User findByUsername(String username) throws Exception {
        String sql = "SELECT " + BASE_COLUMNS + ", password FROM t_user WHERE username = ?";
        return qr.query(sql, new BeanHandler<>(User.class), username);
    }

    public User findById(long id) throws Exception {
        String sql = "SELECT " + BASE_COLUMNS + " FROM t_user WHERE id = ?";
        return qr.query(sql, new BeanHandler<>(User.class), id);
    }

    public User findDetailById(long id) throws Exception {
        String sql = """
                SELECT u.id, u.username, u.nickname, u.phone, u.email, u.avatar, u.role, u.status,
                       u.credit_score AS creditScore, u.campus,
                       u.last_login_time AS lastLoginTime, u.create_time AS createTime, u.update_time AS updateTime,
                       (SELECT COUNT(*) FROM t_product p WHERE p.seller_id = u.id) AS productCount,
                       (SELECT COUNT(*) FROM t_order o WHERE o.buyer_id = u.id OR o.seller_id = u.id) AS orderCount,
                       (SELECT COUNT(*) FROM t_report r WHERE r.reporter_id = u.id) AS reportCount
                FROM t_user u WHERE u.id = ?
                """;
        return qr.query(sql, new BeanHandler<>(User.class), id);
    }

    public List<User> findPage(String keyword, String role, String status, int offset, int limit)
            throws Exception {
        StringBuilder sql = new StringBuilder("SELECT ").append(BASE_COLUMNS)
                .append(" FROM t_user WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, role, status);
        sql.append(" ORDER BY create_time DESC, id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return qr.query(sql.toString(), new BeanListHandler<>(User.class), params.toArray());
    }

    public long count(String keyword, String role, String status) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_user WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, role, status);
        Number number = qr.query(sql.toString(), new ScalarHandler<>(), params.toArray());
        return number == null ? 0 : number.longValue();
    }

    private void appendFilters(StringBuilder sql, List<Object> params,
                               String keyword, String role, String status) {
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (username LIKE ? OR nickname LIKE ? OR phone LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (role != null && !role.isBlank()) {
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
    }

    public int updateStatus(Connection connection, long id, String status) throws Exception {
        return qr.update(connection, "UPDATE t_user SET status = ? WHERE id = ?", status, id);
    }

    public int updatePassword(Connection connection, long id, String password) throws Exception {
        return qr.update(connection, "UPDATE t_user SET password = ? WHERE id = ?", password, id);
    }

    public int updateCredit(Connection connection, long id, int delta) throws Exception {
        return qr.update(connection,
                "UPDATE t_user SET credit_score = GREATEST(0, LEAST(100, credit_score + ?)) WHERE id = ?",
                delta, id);
    }

    public void touchLogin(long id) throws Exception {
        qr.update("UPDATE t_user SET last_login_time = NOW() WHERE id = ?", id);
    }

    public long countAll() throws Exception {
        return count("", "", "");
    }

    public long countUsers() throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_user WHERE role = 'USER'", new ScalarHandler<>());
        return number == null ? 0 : number.longValue();
    }

    public long countToday() throws Exception {
        Number number = qr.query(
                "SELECT COUNT(*) FROM t_user WHERE role = 'USER' AND DATE(create_time) = CURDATE()",
                new ScalarHandler<>());
        return number == null ? 0 : number.longValue();
    }

    public long countBanned() throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_user WHERE status = 'BANNED'", new ScalarHandler<>());
        return number == null ? 0 : number.longValue();
    }
}
