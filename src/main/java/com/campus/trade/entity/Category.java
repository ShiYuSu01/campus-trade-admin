package com.campus.trade.entity;

import java.time.LocalDateTime;

/**
 * 商品分类实体，对应 t_category。
 */
public class Category {

    private Long id;
    private String name;
    private String icon;
    private Integer sort;
    private String status;
    private LocalDateTime createTime;
    private Long productCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public boolean isEnabled() {
        return "ENABLED".equals(status);
    }

    public String getStatusLabel() {
        return isEnabled() ? "启用" : "停用";
    }

    public String getStatusClass() {
        return isEnabled() ? "enabled" : "disabled";
    }

    public String getCreateTimeText() {
        return com.campus.trade.common.DateTimeUtil.format(createTime);
    }
}
