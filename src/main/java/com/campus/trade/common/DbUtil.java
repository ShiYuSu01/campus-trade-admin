package com.campus.trade.common;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * 数据库工具：加载 db.properties，创建 Druid 连接池，并提供事务模板。
 * 支持环境变量覆盖，便于换电脑部署：
 *   CAMPUS_DB_URL / CAMPUS_DB_USER / CAMPUS_DB_PASSWORD
 */
public final class DbUtil {

    private static final DataSource DATA_SOURCE;

    static {
        try {
            Properties properties = new Properties();
            try (var in = DbUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in == null) {
                    throw new IllegalStateException("找不到 db.properties");
                }
                properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
            properties.setProperty("url", env("CAMPUS_DB_URL", properties.getProperty("jdbc.url")));
            properties.setProperty("username", env("CAMPUS_DB_USER", properties.getProperty("jdbc.username")));
            properties.setProperty("password", env("CAMPUS_DB_PASSWORD", properties.getProperty("jdbc.password")));
            properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
            properties.setProperty("testWhileIdle", "true");
            properties.setProperty("timeBetweenEvictionRunsMillis", "60000");
            DATA_SOURCE = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError("数据库连接池初始化失败：" + ex.getMessage());
        }
    }

    private DbUtil() {
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    /**
     * 事务模板：回调内使用同一个 Connection，正常提交，异常回滚。
     */
    public static <T> T inTransaction(TxFunction<T> function) {
        Connection connection = null;
        try {
            connection = getConnection();
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T result = function.apply(connection);
                connection.commit();
                return result;
            } catch (Exception ex) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                if (ex instanceof RuntimeException runtime) {
                    throw runtime;
                }
                throw new BizException("数据库操作失败", ex);
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException ex) {
            throw new BizException("数据库连接失败", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                    // 关闭失败不覆盖业务异常
                }
            }
        }
    }

    @FunctionalInterface
    public interface TxFunction<T> {
        T apply(Connection connection) throws Exception;
    }
}
