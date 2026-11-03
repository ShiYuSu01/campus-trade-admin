package com.campus.trade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 二手商品实体，对应 t_product。
 */
public class Product {

    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String conditionLevel;
    private String campus;
    private String coverImage;
    private String images;
    private String status;
    private String rejectReason;
    private Integer viewCount;
    private Integer favoriteCount;
    private Long auditBy;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段
    private String sellerName;
    private String sellerAvatar;
    private String categoryName;
    private Long orderCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getConditionLevel() {
        return conditionLevel;
    }

    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Long getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(Long auditBy) {
        this.auditBy = auditBy;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
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

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerAvatar() {
        return sellerAvatar;
    }

    public void setSellerAvatar(String sellerAvatar) {
        this.sellerAvatar = sellerAvatar;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public String getStatusLabel() {
        return switch (status == null ? "" : status) {
            case "PENDING" -> "待审核";
            case "APPROVED" -> "已通过";
            case "REJECTED" -> "已驳回";
            case "OFF_SHELF" -> "已下架";
            case "SOLD" -> "已售出";
            default -> status;
        };
    }

    public String getStatusClass() {
        return status == null ? "" : status.toLowerCase().replace('_', '-');
    }

    public String getConditionLabel() {
        return switch (conditionLevel == null ? "" : conditionLevel) {
            case "NEW" -> "全新";
            case "LIKE_NEW" -> "几乎全新";
            case "GOOD" -> "成色良好";
            case "FAIR" -> "正常使用";
            default -> conditionLevel;
        };
    }

    public String getPriceText() {
        return price == null ? "¥0.00" : "¥" + price.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public String getOriginalPriceText() {
        return originalPrice == null ? "" : "¥" + originalPrice.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public String getCreateTimeText() {
        return com.campus.trade.common.DateTimeUtil.format(createTime);
    }

    public String getAuditTimeText() {
        return com.campus.trade.common.DateTimeUtil.format(auditTime);
    }

    public String getShortDescription() {
        if (description == null) {
            return "";
        }
        String text = description.replace('\n', ' ').trim();
        return text.length() <= 48 ? text : text.substring(0, 48) + "…";
    }

    public String getCoverOrDefault() {
        return coverImage == null || coverImage.isBlank()
                ? "/assets/images/products/placeholder.svg"
                : coverImage;
    }

    public List<String> getImageList() {
        List<String> list = new ArrayList<>();
        if (images != null && !images.isBlank()) {
            for (String item : images.split(",")) {
                if (!item.isBlank()) {
                    list.add(item.trim());
                }
            }
        }
        if (list.isEmpty()) {
            list.add(getCoverOrDefault());
        }
        return list;
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    public boolean isOffShelf() {
        return "OFF_SHELF".equals(status);
    }

    public boolean isSold() {
        return "SOLD".equals(status);
    }

    public long getOrderCountSafe() {
        return orderCount == null ? 0 : orderCount;
    }
}
