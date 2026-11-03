package com.campus.trade.entity;

import java.time.LocalDateTime;

/**
 * 用户实体，对应 t_user。
 */
public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String role;
    private String status;
    private Integer creditScore;
    private String campus;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联统计字段（非表字段）
    private Long productCount;
    private Long orderCount;
    private Long reportCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getReportCount() {
        return reportCount;
    }

    public void setReportCount(Long reportCount) {
        this.reportCount = reportCount;
    }

    public String getRoleLabel() {
        return "ADMIN".equals(role) ? "管理员" : "普通用户";
    }

    public String getStatusLabel() {
        return "BANNED".equals(status) ? "已封禁" : "正常";
    }

    public String getStatusClass() {
        return "BANNED".equals(status) ? "banned" : "active";
    }

    public String getInitial() {
        String source = nickname != null && !nickname.isBlank() ? nickname : username;
        return source == null || source.isBlank() ? "?" : source.substring(0, 1);
    }

    public String getCreditClass() {
        int score = creditScore == null ? 100 : creditScore;
        if (score >= 90) {
            return "good";
        }
        if (score >= 70) {
            return "warn";
        }
        return "danger";
    }

    public String getCreateTimeText() {
        return com.campus.trade.common.DateTimeUtil.format(createTime);
    }

    public String getLastLoginTimeText() {
        return com.campus.trade.common.DateTimeUtil.format(lastLoginTime);
    }
}
