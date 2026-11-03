-- ============================================================
-- 校园二手交易平台后台管理系统 · 数据库结构
-- MySQL 8.0+ / utf8mb4
-- 说明：脚本可重复执行，不会删除已有数据
-- ============================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS campus_trade
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_trade;

-- 1. 用户表 -------------------------------------------------
CREATE TABLE IF NOT EXISTS t_user (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password        VARCHAR(200) NOT NULL COMMENT 'PBKDF2 密码摘要',
    nickname        VARCHAR(50)  NOT NULL COMMENT '昵称',
    phone           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar          VARCHAR(255) DEFAULT NULL COMMENT '头像',
    role            VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT 'ADMIN / USER',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / BANNED',
    credit_score    INT          NOT NULL DEFAULT 100 COMMENT '信用分',
    campus          VARCHAR(50)  DEFAULT NULL COMMENT '所在校区',
    last_login_time DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    KEY idx_user_role_status (role, status),
    KEY idx_user_create_time (create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 2. 商品分类表 ---------------------------------------------
CREATE TABLE IF NOT EXISTS t_category (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name        VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon        VARCHAR(20) DEFAULT '📦' COMMENT '图标',
    sort        INT         NOT NULL DEFAULT 0 COMMENT '排序值',
    status      VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED / DISABLED',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_name (name),
    KEY idx_category_sort (sort)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品分类表';

-- 3. 商品表 -------------------------------------------------
CREATE TABLE IF NOT EXISTS t_product (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    seller_id       BIGINT        NOT NULL COMMENT '卖家ID',
    category_id     BIGINT        NOT NULL COMMENT '分类ID',
    title           VARCHAR(120)  NOT NULL COMMENT '商品标题',
    description     TEXT          COMMENT '商品描述',
    price           DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price  DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    condition_level VARCHAR(20)   NOT NULL DEFAULT 'GOOD' COMMENT 'NEW / LIKE_NEW / GOOD / FAIR',
    campus          VARCHAR(50)   DEFAULT NULL COMMENT '交易校区',
    cover_image     VARCHAR(255)  DEFAULT NULL COMMENT '封面图',
    images          TEXT          COMMENT '更多图片，逗号分隔',
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / APPROVED / REJECTED / OFF_SHELF / SOLD',
    reject_reason   VARCHAR(255)  DEFAULT NULL COMMENT '驳回原因',
    view_count      INT           NOT NULL DEFAULT 0 COMMENT '浏览量',
    favorite_count  INT           NOT NULL DEFAULT 0 COMMENT '收藏量',
    audit_by        BIGINT        DEFAULT NULL COMMENT '审核人',
    audit_time      DATETIME      DEFAULT NULL COMMENT '审核时间',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_product_status_time (status, create_time),
    KEY idx_product_category (category_id),
    KEY idx_product_seller (seller_id),
    CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES t_user (id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES t_category (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '二手商品表';

-- 4. 订单表 -------------------------------------------------
CREATE TABLE IF NOT EXISTS t_order (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no        VARCHAR(32)   NOT NULL COMMENT '订单号',
    product_id      BIGINT        NOT NULL COMMENT '商品ID',
    buyer_id        BIGINT        NOT NULL COMMENT '买家ID',
    seller_id       BIGINT        NOT NULL COMMENT '卖家ID',
    amount          DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    status          VARCHAR(30)   NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态',
    delivery_method VARCHAR(20)   NOT NULL DEFAULT 'SELF_PICKUP' COMMENT 'SELF_PICKUP / EXPRESS',
    address         VARCHAR(255)  DEFAULT NULL COMMENT '收货地址 / 面交地点',
    remark          VARCHAR(255)  DEFAULT NULL COMMENT '买家备注',
    pay_time        DATETIME      DEFAULT NULL COMMENT '支付时间',
    ship_time       DATETIME      DEFAULT NULL COMMENT '发货时间',
    finish_time     DATETIME      DEFAULT NULL COMMENT '完成时间',
    cancel_time     DATETIME      DEFAULT NULL COMMENT '取消时间',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_status_time (status, create_time),
    KEY idx_order_buyer (buyer_id),
    KEY idx_order_seller (seller_id),
    KEY idx_order_product (product_id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES t_product (id),
    CONSTRAINT fk_order_buyer FOREIGN KEY (buyer_id) REFERENCES t_user (id),
    CONSTRAINT fk_order_seller FOREIGN KEY (seller_id) REFERENCES t_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单表';

-- 5. 举报表 -------------------------------------------------
CREATE TABLE IF NOT EXISTS t_report (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    reporter_id     BIGINT       NOT NULL COMMENT '举报人',
    target_type     VARCHAR(20)  NOT NULL COMMENT 'PRODUCT / ORDER / USER',
    target_id       BIGINT       NOT NULL COMMENT '被举报对象ID',
    reason_type     VARCHAR(30)  NOT NULL COMMENT 'FAKE / ILLEGAL / CHEAT / ABUSE / OTHER',
    description     TEXT         COMMENT '举报描述',
    evidence_images TEXT         COMMENT '证据图片，逗号分隔',
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / PROCESSING / RESOLVED / REJECTED',
    handler_id      BIGINT       DEFAULT NULL COMMENT '处理人',
    handle_result   VARCHAR(500) DEFAULT NULL COMMENT '处理说明',
    handle_action   VARCHAR(30)  DEFAULT NULL COMMENT 'NONE / OFF_SHELF / BAN_USER / WARN',
    handled_at      DATETIME     DEFAULT NULL COMMENT '处理时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_report_status_time (status, create_time),
    KEY idx_report_target (target_type, target_id),
    KEY idx_report_reporter (reporter_id),
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES t_user (id),
    CONSTRAINT fk_report_handler FOREIGN KEY (handler_id) REFERENCES t_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '举报表';

-- 6. 审核日志表 ---------------------------------------------
CREATE TABLE IF NOT EXISTS t_audit_log (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    biz_type      VARCHAR(20)  NOT NULL COMMENT 'PRODUCT / ORDER / REPORT / USER / CATEGORY',
    biz_id        BIGINT       NOT NULL COMMENT '业务ID',
    action        VARCHAR(50)  NOT NULL COMMENT '操作动作',
    operator_id   BIGINT       DEFAULT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50)  DEFAULT NULL COMMENT '操作人名称',
    before_status VARCHAR(30)  DEFAULT NULL COMMENT '操作前状态',
    after_status  VARCHAR(30)  DEFAULT NULL COMMENT '操作后状态',
    remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_log_biz (biz_type, biz_id),
    KEY idx_log_create_time (create_time),
    KEY idx_log_operator (operator_id),
    CONSTRAINT fk_log_operator FOREIGN KEY (operator_id) REFERENCES t_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '审核日志表';
