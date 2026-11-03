package com.campus.trade.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * 首次启动时自动建表并写入演示数据。
 * 判断依据：当前数据库不存在 t_user 表。
 */
public final class DatabaseInitializer {

    private static final Logger LOG = Logger.getLogger(DatabaseInitializer.class.getName());

    private DatabaseInitializer() {
    }

    public static void initIfNeeded() throws Exception {
        try (Connection connection = DbUtil.getConnection()) {
            if (tableExists(connection, "t_user")) {
                LOG.info("数据库已初始化，跳过建表与演示数据。");
                return;
            }
            LOG.info("检测到空数据库，开始执行 schema.sql ...");
            runScript(connection, "sql/schema.sql");
            LOG.info("表结构创建完成，开始写入演示数据 ...");
            runScript(connection, "sql/data.sql");
            LOG.info("数据库初始化完成，演示数据已就绪。");
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws Exception {
        String sql = "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = DATABASE() AND table_name = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getLong(1) > 0;
            }
        }
    }

    private static void runScript(Connection connection, String resource) throws Exception {
        String content;
        try (InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("找不到 SQL 脚本：" + resource);
            }
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("#")) {
                        continue;
                    }
                    builder.append(line).append('\n');
                }
            }
            content = builder.toString();
        }

        int executed = 0;
        for (String statementText : content.split(";")) {
            String sql = statementText.trim();
            if (sql.isEmpty()) {
                continue;
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
                executed++;
            } catch (Exception ex) {
                throw new IllegalStateException("执行 SQL 失败（" + resource + "）："
                        + sql.substring(0, Math.min(120, sql.length())) + " ... 原因：" + ex.getMessage(), ex);
            }
        }
        LOG.info(resource + " 执行完成，共 " + executed + " 条语句。");
    }
}
