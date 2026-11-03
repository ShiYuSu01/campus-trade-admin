package com.campus.trade.entity;

import java.time.LocalDateTime;

/**
 * 审核日志实体，对应 t_audit_log。
 */
public class AuditLog {

    private Long id;
    private String bizType;
    private Long bizId;
    private String action;
    private Long operatorId;
    private String operatorName;
    private String beforeStatus;
    private String afterStatus;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public String getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getBizTypeLabel() {
        return switch (bizType == null ? "" : bizType) {
            case "PRODUCT" -> "商品";
            case "ORDER" -> "订单";
            case "REPORT" -> "举报";
            case "USER" -> "用户";
            case "CATEGORY" -> "分类";
            default -> bizType;
        };
    }

    public String getActionLabel() {
        return switch (action == null ? "" : action) {
            case "SUBMIT" -> "提交审核";
            case "APPROVE" -> "审核通过";
            case "REJECT" -> "审核驳回";
            case "OFF_SHELF" -> "下架";
            case "RESTORE" -> "重新上架";
            case "DELETE" -> "删除";
            case "HANDLE" -> "处理举报";
            case "PROCESS" -> "开始处理";
            case "CANCEL" -> "取消订单";
            case "SHIP" -> "确认发货";
            case "COMPLETE" -> "确认完成";
            case "REFUND" -> "退款";
            case "BAN" -> "封禁用户";
            case "UNBAN" -> "解封用户";
            case "RESET_PASSWORD" -> "重置密码";
            case "WARN" -> "警告扣分";
            case "CREATE" -> "新增分类";
            case "UPDATE" -> "修改分类";
            case "TOGGLE" -> "启用/停用";
            default -> action;
        };
    }

    public String getCreateTimeText() {
        return com.campus.trade.common.DateTimeUtil.format(createTime);
    }

    public String getStatusChangeText() {
        if (beforeStatus == null && afterStatus == null) {
            return "-";
        }
        return (beforeStatus == null ? "—" : beforeStatus) + " → " + (afterStatus == null ? "—" : afterStatus);
    }
}
