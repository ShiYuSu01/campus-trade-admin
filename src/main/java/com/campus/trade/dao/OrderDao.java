package com.campus.trade.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.campus.trade.common.DbUtil;
import com.campus.trade.dto.TrendPoint;
import com.campus.trade.entity.Order;

/**
 * 订单数据访问。
 */
public class OrderDao {

    private final QueryRunner qr = new QueryRunner(DbUtil.getDataSource());

    private static final String BASE_SELECT = """
            SELECT o.id, o.order_no AS orderNo, o.product_id AS productId, o.buyer_id AS buyerId,
                   o.seller_id AS sellerId, o.amount, o.status, o.delivery_method AS deliveryMethod,
                   o.address, o.remark, o.pay_time AS payTime, o.ship_time AS shipTime,
                   o.finish_time AS finishTime, o.cancel_time AS cancelTime,
                   o.create_time AS createTime, o.update_time AS updateTime,
                   p.title AS productTitle, p.cover_image AS productCover,
                   b.nickname AS buyerName, b.phone AS buyerPhone,
                   s.nickname AS sellerName, s.phone AS sellerPhone
            FROM t_order o
            JOIN t_product p ON o.product_id = p.id
            JOIN t_user b ON o.buyer_id = b.id
            JOIN t_user s ON o.seller_id = s.id
            """;

    public Order findById(long id) throws Exception {
        return qr.query(BASE_SELECT + " WHERE o.id = ?", new BeanHandler<>(Order.class), id);
    }

    public List<Order> findPage(String keyword, String status, int offset, int limit) throws Exception {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status);
        sql.append(" ORDER BY o.create_time DESC, o.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return qr.query(sql.toString(), new BeanListHandler<>(Order.class), params.toArray());
    }

    public long count(String keyword, String status) throws Exception {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM t_order o
                JOIN t_product p ON o.product_id = p.id
                JOIN t_user b ON o.buyer_id = b.id
                JOIN t_user s ON o.seller_id = s.id
                WHERE 1 = 1
                """);
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status);
        Number number = qr.query(sql.toString(), new ScalarHandler<>(), params.toArray());
        return number == null ? 0 : number.longValue();
    }

    private void appendFilters(StringBuilder sql, List<Object> params, String keyword, String status) {
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (o.order_no LIKE ? OR p.title LIKE ? OR b.nickname LIKE ? OR s.nickname LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND o.status = ?");
            params.add(status);
        }
    }

    /**
     * 管理员流转订单状态，并按目标状态补时间戳。
     */
    public int updateStatus(Connection connection, long id, String status) throws Exception {
        String timeColumn = switch (status) {
            case "PAID" -> "pay_time";
            case "SHIPPED" -> "ship_time";
            case "COMPLETED" -> "finish_time";
            case "CANCELLED", "REFUNDED" -> "cancel_time";
            default -> null;
        };
        String sql = "UPDATE t_order SET status = ?"
                + (timeColumn == null ? "" : ", " + timeColumn + " = NOW()")
                + " WHERE id = ?";
        return qr.update(connection, sql, status, id);
    }

    public long countAll() throws Exception {
        return count(null, null);
    }

    public long countByStatus(String status) throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_order WHERE status = ?",
                new ScalarHandler<>(), status);
        return number == null ? 0 : number.longValue();
    }

    public long countToday() throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_order WHERE DATE(create_time) = CURDATE()",
                new ScalarHandler<>());
        return number == null ? 0 : number.longValue();
    }

    public BigDecimal sumCompletedAmount() throws Exception {
        BigDecimal value = qr.query(
                "SELECT COALESCE(SUM(amount), 0) FROM t_order WHERE status = 'COMPLETED'",
                new ScalarHandler<>());
        return value == null ? BigDecimal.ZERO : value;
    }

    public List<Order> findRecent(int limit) throws Exception {
        return qr.query(BASE_SELECT + " ORDER BY o.create_time DESC, o.id DESC LIMIT ?",
                new BeanListHandler<>(Order.class), limit);
    }

    public List<Order> findByBuyer(long buyerId, int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE o.buyer_id = ? ORDER BY o.create_time DESC LIMIT ?",
                new BeanListHandler<>(Order.class), buyerId, limit);
    }

    public List<Order> findBySeller(long sellerId, int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE o.seller_id = ? ORDER BY o.create_time DESC LIMIT ?",
                new BeanListHandler<>(Order.class), sellerId, limit);
    }

    /**
     * 近 N 天订单量与成交额，供看板柱状图使用。
     */
    public List<TrendPoint> trend(int days) throws Exception {
        int safeDays = Math.max(1, Math.min(days, 30));
        String sql = "SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS `date`, "
                + "COUNT(*) AS count, COALESCE(SUM(amount), 0) AS amount "
                + "FROM t_order WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL " + (safeDays - 1) + " DAY) "
                + "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d') ORDER BY `date`";
        return qr.query(sql, new BeanListHandler<>(TrendPoint.class));
    }
}
