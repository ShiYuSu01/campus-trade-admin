package com.campus.trade.dao;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.campus.trade.common.DbUtil;
import com.campus.trade.entity.Category;

/**
 * 商品分类数据访问。
 */
public class CategoryDao {

    private final QueryRunner qr = new QueryRunner(DbUtil.getDataSource());

    private static final String BASE_SELECT = """
            SELECT c.id, c.name, c.icon, c.sort, c.status, c.create_time AS createTime,
                   (SELECT COUNT(*) FROM t_product p WHERE p.category_id = c.id) AS productCount
            FROM t_category c
            """;

    public List<Category> findAll() throws Exception {
        return qr.query(BASE_SELECT + " ORDER BY c.sort ASC, c.id ASC", new BeanListHandler<>(Category.class));
    }

    public List<Category> findEnabled() throws Exception {
        return qr.query(BASE_SELECT + " WHERE c.status = 'ENABLED' ORDER BY c.sort ASC, c.id ASC",
                new BeanListHandler<>(Category.class));
    }

    public Category findById(long id) throws Exception {
        return qr.query(BASE_SELECT + " WHERE c.id = ?", new BeanHandler<>(Category.class), id);
    }

    public long insert(Connection connection, Category category) throws Exception {
        String sql = "INSERT INTO t_category (name, icon, sort, status) VALUES (?, ?, ?, ?)";
        Number number = qr.insert(connection, sql, new ScalarHandler<>(),
                category.getName(), category.getIcon(), category.getSort(), category.getStatus());
        return number == null ? 0 : number.longValue();
    }

    public int update(Connection connection, Category category) throws Exception {
        String sql = "UPDATE t_category SET name = ?, icon = ?, sort = ? WHERE id = ?";
        return qr.update(connection, sql, category.getName(), category.getIcon(),
                category.getSort(), category.getId());
    }

    public int updateStatus(Connection connection, long id, String status) throws Exception {
        return qr.update(connection, "UPDATE t_category SET status = ? WHERE id = ?", status, id);
    }

    public int delete(Connection connection, long id) throws Exception {
        return qr.update(connection, "DELETE FROM t_category WHERE id = ?", id);
    }

    public long countProducts(long categoryId) throws Exception {
        Number number = qr.query("SELECT COUNT(*) FROM t_product WHERE category_id = ?",
                new ScalarHandler<>(), categoryId);
        return number == null ? 0 : number.longValue();
    }

    public boolean existsName(String name, Long excludeId) throws Exception {
        String sql = "SELECT COUNT(*) FROM t_category WHERE name = ? AND (? IS NULL OR id <> ?)";
        Number number = qr.query(sql, new ScalarHandler<>(), name, excludeId, excludeId);
        return number != null && number.longValue() > 0;
    }
}
