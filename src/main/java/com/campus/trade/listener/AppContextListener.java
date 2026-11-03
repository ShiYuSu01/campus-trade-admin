package com.campus.trade.listener;

import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import com.alibaba.druid.pool.DruidDataSource;
import com.campus.trade.common.DatabaseInitializer;
import com.campus.trade.common.DbUtil;

/**
 * 应用启动时验证数据库、自动初始化表结构和演示数据。
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DbUtil.getDataSource();
            DatabaseInitializer.initIfNeeded();
            sce.getServletContext().setAttribute("appStartedAt", System.currentTimeMillis());
            LOG.info("校园二手交易平台后台启动成功。");
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, "应用启动失败：请检查 MySQL 是否启动、db.properties 账号密码是否正确。", ex);
            throw new IllegalStateException("应用启动失败，数据库不可用", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (DbUtil.getDataSource() instanceof DruidDataSource druid) {
                druid.close();
                LOG.info("Druid 连接池已关闭。");
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "关闭连接池时出现异常", ex);
        }
    }
}
