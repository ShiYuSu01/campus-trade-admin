package com.campus.trade.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.campus.trade.common.DbUtil;
import com.campus.trade.dto.CategoryStat;
import com.campus.trade.entity.Product;

/**
 * 商品数据访问。
 */
public class ProductDao {

    private final QueryRunner qr = new QueryRunner(DbUtil.getDataSource());

    private static final String BASE_SELECT = """
            SELECT p.id, p.seller_id AS sellerId, p.category_id AS categoryId, p.title, p.description,
                   p.price, p.original_price AS originalPrice, p.condition_level AS conditionLevel,
                   p.campus, p.cover_image AS coverImage, p.images, p.status, p.reject_reason AS rejectReason,
                   p.view_count AS viewCount, p.favorite_count AS favoriteCount,
                   p.audit_by AS auditBy, p.audit_time AS auditTime,
                   p.create_time AS createTime, p.update_time AS updateTime,
                   u.nickname AS sellerName, u.avatar AS sellerAvatar, c.name AS categoryName,
                   (SELECT COUNT(*) FROM t_order o WHERE o.product_id = p.id) AS orderCount
            FROM t_product p
            JOIN t_user u ON p.seller_id = u.id
            JOIN t_category c ON p.category_id = c.id
            """;

    public Product findById(long id) throws Exception {
        return qr.query(BASE_SELECT + " WHERE p.id = ?", new BeanHandler<>(Product.class), id);
    }

    public List<Product> findPage(String keyword, String status, Long categoryId, int offset, int limit)
            throws Exception {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status, categoryId);
        sql.append(" ORDER BY p.create_time DESC, p.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return qr.query(sql.toString(), new BeanListHandler<>(Product.class), params.toArray());
    }

    public long count(String keyword, String status, Long categoryId) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_product p WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status, categoryId);
        Number number = qr.query(sql.toString(), new ScalarHandler<>(), params.toArray());
        return number == null ? 0 : number.longValue();
    }

    private void appendFilters(StringBuilder sql, List<Object> params,
                               String keyword, String status, Long categoryId) {
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (p.title LIKE ? OR p.description LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND p.status = ?");
            params.add(status);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
    }

    public int audit(Connection connection, long id, String status, String reason, long auditBy)
            throws Exception {
        String sql = "UPDATE t_product SET status = ?, reject_reason = ?, audit_by = ?, "
                + "audit_time = NOW() WHERE id = ?";
        return qr.update(connection, sql, status, reason, auditBy, id);
    }

    public int updateStatus(Connection connection, long id, String status, String reason) throws Exception {
        return qr.update(connection,
                "UPDATE t_product SET status = ?, reject_reason = ? WHERE id = ?",
                status, reason, id);
    }

    public int delete(Connection connection, long id) throws Exception {
        return qr.update(connection, "DELETE FROM t_product WHERE id = ?", id);
    }

    public long countAll() throws Exception {
        return count(null, null, null);
    }

    public long countByStatus(String status) throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_product WHERE status = ?",
                new ScalarHandler<>(), status);
        return number == null ? 0 : number.longValue();
    }

    public long countToday() throws Exception {
        Number number = qr.query(
                "SELECT COUNT(*) FROM t_product WHERE DATE(create_time) = CURDATE()", new ScalarHandler<>());
        return number == null ? 0 : number.longValue();
    }

    public long countOrders(long productId) throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_order WHERE product_id = ?",
                new ScalarHandler<>(), productId);
        return number == null ? 0 : number.longValue();
    }

    public List<Product> findTopPending(int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE p.status = 'PENDING' ORDER BY p.create_time ASC LIMIT ?",
                new BeanListHandler<>(Product.class), limit);
    }

    public List<Product> findBySeller(long sellerId, int limit) throws Exception {
        return qr.query(BASE_SELECT + " WHERE p.seller_id = ? ORDER BY p.create_time DESC LIMIT ?",
                new BeanListHandler<>(Product.class), sellerId, limit);
    }

    public List<CategoryStat> categoryStats() throws Exception {
        String sql = """
                SELECT c.name AS name, COUNT(p.id) AS count
                FROM t_category c
                LEFT JOIN t_product p ON p.category_id = c.id
                GROUP BY c.id, c.name
                ORDER BY count DESC, c.sort ASC
                """;
        return qr.query(sql, new BeanListHandler<>(CategoryStat.class));
    }
}
