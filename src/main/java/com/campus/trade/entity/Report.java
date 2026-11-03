package com.campus.trade.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 举报实体，对应 t_report。
 */
public class Report {

    private Long id;
    private Long reporterId;
    private String targetType;
    private Long targetId;
    private String reasonType;
    private String description;
    private String evidenceImages;
    private String status;
    private Long handlerId;
    private String handleResult;
    private String handleAction;
    private LocalDateTime handledAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段
    private String reporterName;
    private String handlerName;
    private String targetTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getReasonType() {
        return reasonType;
    }

    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvidenceImages() {
        return evidenceImages;
    }

    public void setEvidenceImages(String evidenceImages) {
        this.evidenceImages = evidenceImages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public String getHandleAction() {
        return handleAction;
    }

    public void setHandleAction(String handleAction) {
        this.handleAction = handleAction;
    }

    public LocalDateTime getHandledAt() {
        return handledAt;
    }

    public void setHandledAt(LocalDateTime handledAt) {
        this.handledAt = handledAt;
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

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public String getStatusLabel() {
        return switch (status == null ? "" : status) {
            case "PENDING" -> "待处理";
            case "PROCESSING" -> "处理中";
            case "RESOLVED" -> "已解决";
            case "REJECTED" -> "已驳回";
            default -> status;
        };
    }

    public String getStatusClass() {
        return status == null ? "" : status.toLowerCase();
    }

    public String getTargetTypeLabel() {
        return switch (targetType == null ? "" : targetType) {
            case "PRODUCT" -> "商品";
            case "ORDER" -> "订单";
            case "USER" -> "用户";
            default -> targetType;
        };
    }

    public String getReasonLabel() {
        return switch (reasonType == null ? "" : reasonType) {
            case "FAKE" -> "虚假描述";
            case "ILLEGAL" -> "违规商品";
            case "CHEAT" -> "欺诈风险";
            case "ABUSE" -> "辱骂骚扰";
            case "OTHER" -> "其他问题";
            default -> reasonType;
        };
    }

    public String getActionLabel() {
        return switch (handleAction == null ? "" : handleAction) {
            case "OFF_SHELF" -> "商品下架";
            case "BAN_USER" -> "封禁用户";
            case "WARN" -> "警告扣分";
            case "NONE" -> "无附加处罚";
            default -> "-";
        };
    }

    public String getCreateTimeText() {
        return com.campus.trade.common.DateTimeUtil.format(createTime);
    }

    public String getHandledAtText() {
        return com.campus.trade.common.DateTimeUtil.format(handledAt);
    }

    public List<String> getEvidenceList() {
        List<String> list = new ArrayList<>();
        if (evidenceImages != null && !evidenceImages.isBlank()) {
            for (String item : evidenceImages.split(",")) {
                if (!item.isBlank()) {
                    list.add(item.trim());
                }
            }
        }
        return list;
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isProcessing() {
        return "PROCESSING".equals(status);
    }

    public boolean isResolved() {
        return "RESOLVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }
}
