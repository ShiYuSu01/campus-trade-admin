package com.campus.trade.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 健康检查返回体（轻量 DTO）：用于部署后一条命令判断“应用是否起来 + 数据库是否连通”。
 * 不依赖任何第三方 JSON 库，直接拼装，避免为一个小接口引入新依赖。
 */
public class HealthStatus {

    private final String app = "campus-trade-admin";
    private String status = "UP";
    private String javaVersion = System.getProperty("java.version");
    private String serverInfo = "unknown";
    private String contextPath = "";
    private boolean databaseOk;
    private String databaseMessage = "";
    private long databaseCostMs;
    private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public String getStatus() {
        return status;
    }

    public HealthStatus status(String status) {
        this.status = status;
        return this;
    }

    public HealthStatus serverInfo(String serverInfo) {
        this.serverInfo = serverInfo;
        return this;
    }

    public HealthStatus contextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public HealthStatus database(boolean ok, String message, long costMs) {
        this.databaseOk = ok;
        this.databaseMessage = message;
        this.databaseCostMs = costMs;
        return this;
    }

    /** 输出 JSON 字符串（字段固定，无需序列化库） */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"app\":\"").append(app).append("\",");
        sb.append("\"status\":\"").append(status).append("\",");
        sb.append("\"javaVersion\":\"").append(javaVersion).append("\",");
        sb.append("\"serverInfo\":\"").append(serverInfo).append("\",");
        sb.append("\"contextPath\":\"").append(contextPath).append("\",");
        sb.append("\"time\":\"").append(time).append("\",");
        sb.append("\"database\":{");
        sb.append("\"ok\":").append(databaseOk).append(",");
        sb.append("\"message\":\"").append(escape(databaseMessage)).append("\",");
        sb.append("\"costMs\":").append(databaseCostMs);
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}
