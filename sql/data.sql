-- ============================================================
-- 校园二手交易平台后台管理系统 · 演示数据
-- 所有账号初始密码：123456
-- 管理员：admin / 123456
-- 说明：使用 INSERT IGNORE，重复执行不会产生重复数据
-- ============================================================

SET NAMES utf8mb4;
USE campus_trade;

-- 用户 -------------------------------------------------------
INSERT IGNORE INTO t_user
(id, username, password, nickname, phone, email, role, status, credit_score, campus, last_login_time, create_time)
VALUES
(1, 'admin', 'pbkdf2$120000$fe4807c9425eb0c83d371fb5d63c75ef$39d1bbde96ed18b66417f21ce1deeaf85d1733d56e483bf7193fae9ba20e7211', '平台管理员', '13800000000', 'admin@campus.edu.cn', 'ADMIN', 'ACTIVE', 100, '行政楼', NOW(), DATE_SUB(NOW(), INTERVAL 120 DAY)),
(2, 'linxia', 'pbkdf2$120000$90fc047bd7ac554c24549f4315cc2aa5$37451749e7a00c51dbe6ac8bdc480faeeae09e2dfefad487d0fecdbac012be1e', '林晓', '13800000002', 'linxia@campus.edu.cn', 'USER', 'ACTIVE', 98, '东区 3 号楼', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 96 DAY)),
(3, 'chenyu', 'pbkdf2$120000$6b6cc8bbd768494764c2577c8795bbf9$d5f0a9c695ed05a55f3ccfa159fc8c1646a58e88a5d032773361ec43abc67981', '陈宇', '13800000003', 'chenyu@campus.edu.cn', 'USER', 'ACTIVE', 92, '西区 1 号楼', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 80 DAY)),
(4, 'zhaolei', 'pbkdf2$120000$6d894f5d08c3f697297164b036f5cce2$e1e6509e856eb7f0bd99311527173b973255969878b195d520ff09c6e39b4c81', '赵磊', '13800000004', 'zhaolei@campus.edu.cn', 'USER', 'ACTIVE', 95, '南区 5 号楼', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 70 DAY)),
(5, 'sunqi', 'pbkdf2$120000$5cda0b171b62689aff9bb9e4f3ca2334$7da7afadc20b37ed957d7795a3092f12918cc5d935556e0f34f7aef600e46e11', '孙琪', '13800000005', 'sunqi@campus.edu.cn', 'USER', 'ACTIVE', 88, '北区 2 号楼', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 60 DAY)),
(6, 'wujia', 'pbkdf2$120000$53e48dc10bed471c6a2469b8e0ed4f2b$725e97da88e526e44296aee909d7292dfbaecf98431f6d17c35c538f6273a25f', '吴佳', '13800000006', 'wujia@campus.edu.cn', 'USER', 'ACTIVE', 91, '东区 8 号楼', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY)),
(7, 'baduser', 'pbkdf2$120000$09efb0c74b30b6a78b251b8519fc3bb8$c7e351c2bb01ccd51e074e1c3f99d707d3b2b99a321853fb108d56ad46b18d34', '郑某', '13800000007', 'baduser@campus.edu.cn', 'USER', 'ACTIVE', 55, '西区 6 号楼', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY));

-- 分类 -------------------------------------------------------
INSERT IGNORE INTO t_category (id, name, icon, sort, status) VALUES
(1, '教材书籍', '📚', 1, 'ENABLED'),
(2, '数码电子', '💻', 2, 'ENABLED'),
(3, '生活用品', '🧺', 3, 'ENABLED'),
(4, '运动户外', '🏸', 4, 'ENABLED'),
(5, '服饰鞋包', '🧥', 5, 'ENABLED'),
(6, '乐器其他', '🎸', 6, 'ENABLED');

-- 商品 -------------------------------------------------------
INSERT IGNORE INTO t_product
(id, seller_id, category_id, title, description, price, original_price, condition_level, campus, cover_image, images, status, reject_reason, view_count, favorite_count, audit_by, audit_time, create_time)
VALUES
(1, 2, 1, '高等数学同济第七版（上下册）', '同济大学第七版，上下册合售。书内有少量铅笔笔记，重点章节有荧光笔标注，不影响阅读。期末复习很好用，东区宿舍楼下自提。', 25.00, 89.00, 'GOOD', '东区', '/assets/images/products/p1.svg', '/assets/images/products/p1.svg', 'PENDING', NULL, 36, 8, NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(2, 3, 2, '87 键机械键盘 青轴', '自用机械键盘，青轴手感清脆，键帽无打油，所有按键触发正常。换了无线键盘所以出掉，附赠拔键器。', 180.00, 299.00, 'LIKE_NEW', '西区', '/assets/images/products/p2.svg', '/assets/images/products/p2.svg', 'PENDING', NULL, 58, 14, NULL, NULL, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(3, 4, 4, '捷安特山地自行车 27 速', '骑行两年，正常使用痕迹，刹车和变速都已保养。车架无裂痕，送车锁和打气筒。仅限校内面交，可试骑。', 650.00, 1899.00, 'GOOD', '南区', '/assets/images/products/p3.svg', '/assets/images/products/p3.svg', 'SOLD', NULL, 230, 41, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(4, 2, 3, '宿舍护眼台灯 三档调光', 'LED 护眼台灯，三档色温，带 USB 充电口。用了一个学期，功能完好，因毕业清宿舍低价出。', 45.00, 129.00, 'GOOD', '东区', '/assets/images/products/p4.svg', '/assets/images/products/p4.svg', 'SOLD', NULL, 112, 19, 1, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(5, 5, 6, '尤克里里 23 寸 单板', '23 寸桃花芯单板尤克里里，音色温暖，配琴包、调音器和备用琴弦。买来弹了几个月，现在闲置。', 120.00, 360.00, 'LIKE_NEW', '北区', '/assets/images/products/p5.svg', '/assets/images/products/p5.svg', 'SOLD', NULL, 96, 22, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(6, 6, 4, '羽毛球拍一对 含手胶', '碳素羽毛球拍一对，4U 重量，适合新手和日常锻炼。手胶刚换过，送三个羽毛球。', 80.00, 220.00, 'GOOD', '东区', '/assets/images/products/p6.svg', '/assets/images/products/p6.svg', 'APPROVED', NULL, 77, 10, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 7, 3, '大牌化妆品小样套装', '专柜小样套装，包含精华、面霜和口红试用装。包装完整，保质期到明年。可小刀，支持自提。', 30.00, 199.00, 'NEW', '西区', '/assets/images/products/p7.svg', '/assets/images/products/p7.svg', 'REJECTED', '无法提供正规购买凭证，且商品保质期信息不完整，请补充后重新提交', 44, 3, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(8, 3, 3, '小功率电煮锅 宿舍神器', '600W 小电煮锅，可煮面、煮粥，带蒸笼。因宿舍电路限制闲置，介意勿拍。', 60.00, 159.00, 'GOOD', '西区', '/assets/images/products/p8.svg', '/assets/images/products/p8.svg', 'OFF_SHELF', NULL, 148, 12, 1, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(9, 4, 2, 'iPad 2021 64G WiFi 版', 'iPad 第九代 64G 深空灰，屏幕无划痕，电池健康度 89%。配保护壳和钢化膜，适合网课和记笔记。', 1500.00, 2499.00, 'GOOD', '南区', '/assets/images/products/p9.svg', '/assets/images/products/p9.svg', 'SOLD', NULL, 412, 67, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
(10, 6, 1, '考研英语真题 2010-2024', '考研英语一真题，2010 到 2024 年，答案解析齐全。部分年份有做题痕迹，介意的同学慎拍。', 20.00, 68.00, 'FAIR', '东区', '/assets/images/products/p10.svg', '/assets/images/products/p10.svg', 'SOLD', NULL, 89, 7, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(11, 5, 2, '蓝牙耳机 半入耳式', '半入耳蓝牙耳机，连接稳定，续航约 4 小时，充电仓正常。右耳偶尔有轻微底噪，低价出。', 90.00, 249.00, 'GOOD', '北区', '/assets/images/products/p11.svg', '/assets/images/products/p11.svg', 'PENDING', NULL, 67, 9, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(12, 2, 5, '冬季羽绒服 女款 M 码', '90% 白鸭绒羽绒服，M 码，米白色，穿过两次，无污渍无破损。买大了所以出，可试穿。', 200.00, 699.00, 'LIKE_NEW', '东区', '/assets/images/products/p12.svg', '/assets/images/products/p12.svg', 'SOLD', NULL, 134, 25, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(13, 3, 3, '桌面收纳架 三层', '宿舍桌面三层收纳架，免安装，承重好。放书和护肤品都很稳，毕业清仓。', 35.00, 99.00, 'GOOD', '西区', '/assets/images/products/p13.svg', '/assets/images/products/p13.svg', 'APPROVED', NULL, 52, 6, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(14, 6, 4, '斯伯丁篮球 7 号', '斯伯丁 7 号室外篮球，打了一个学期，表皮有正常磨损，气密性良好，送球针。', 55.00, 159.00, 'GOOD', '东区', '/assets/images/products/p14.svg', '/assets/images/products/p14.svg', 'APPROVED', NULL, 61, 8, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 订单 -------------------------------------------------------
INSERT IGNORE INTO t_order
(id, order_no, product_id, buyer_id, seller_id, amount, status, delivery_method, address, remark, pay_time, ship_time, finish_time, cancel_time, create_time)
VALUES
(1001, 'CT202610250001', 9, 2, 4, 1500.00, 'COMPLETED', 'SELF_PICKUP', '南区图书馆门口', '周六下午面交', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1002, 'CT202610290002', 3, 5, 4, 650.00, 'SHIPPED', 'SELF_PICKUP', '南区 5 号楼楼下', '已约好周日上午试骑', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1003, 'CT202610300003', 4, 6, 2, 45.00, 'PAID', 'SELF_PICKUP', '东区 3 号楼门口', '晚上七点后取', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1004, 'CT202610310004', 5, 2, 5, 120.00, 'PENDING_PAYMENT', 'EXPRESS', '东区 3 号楼 302', '请帮忙包好琴', NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1005, 'CT202610280005', 6, 3, 6, 80.00, 'CANCELLED', 'SELF_PICKUP', '东区操场', '临时有事，取消交易', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1006, 'CT202610270006', 10, 4, 6, 20.00, 'COMPLETED', 'EXPRESS', '南区 5 号楼 210', '发顺丰，谢谢', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1007, 'CT202610310007', 12, 5, 2, 200.00, 'REFUNDING', 'EXPRESS', '北区 2 号楼 108', '尺码不合适，申请退款', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 举报 -------------------------------------------------------
INSERT IGNORE INTO t_report
(id, reporter_id, target_type, target_id, reason_type, description, status, handler_id, handle_result, handle_action, handled_at, create_time)
VALUES
(1, 6, 'PRODUCT', 7, 'FAKE', '收到的化妆品小样和专柜正品差别很大，包装印刷粗糙，怀疑是假货。卖家拒绝提供购买凭证，希望平台核实。', 'PENDING', NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(2, 5, 'USER', 7, 'ABUSE', '在交易沟通中卖家多次使用侮辱性语言，并且拒绝按约定退款。聊天记录已截图，请平台处理。', 'PENDING', NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 2, 'PRODUCT', 8, 'ILLEGAL', '该商品为宿舍违规大功率电器，存在安全隐患，请平台下架。', 'RESOLVED', 1, '经核实为违规电器，已下架商品并提醒卖家遵守宿舍用电规定。', 'OFF_SHELF', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 3, 'ORDER', 1005, 'OTHER', '买家临时取消订单，但商品已经被我预留了两天，希望平台协调。', 'REJECTED', 1, '订单为买家主动取消，卖家可重新上架商品，本次举报不成立。', 'NONE', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 2, 'PRODUCT', 3, 'CHEAT', '卖家要求绕过平台私下转账，且提供的车辆信息与描述不一致，怀疑存在欺诈风险。', 'PROCESSING', 1, '已联系双方补充交易凭证和车辆照片，等待进一步核实。', 'NONE', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 审核日志 ---------------------------------------------------
INSERT IGNORE INTO t_audit_log
(id, biz_type, biz_id, action, operator_id, operator_name, before_status, after_status, remark, create_time)
VALUES
(1, 'PRODUCT', 9, 'APPROVE', 1, '平台管理员', 'PENDING', 'APPROVED', '商品信息完整，图片清晰，予以通过', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 'PRODUCT', 3, 'APPROVE', 1, '平台管理员', 'PENDING', 'APPROVED', '商品信息完整，予以通过', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, 'PRODUCT', 8, 'OFF_SHELF', 1, '平台管理员', 'APPROVED', 'OFF_SHELF', '违规电器，依据宿舍管理规定下架', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 'REPORT', 3, 'HANDLE', 1, '平台管理员', 'PENDING', 'RESOLVED', '举报成立，商品已下架', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 'PRODUCT', 7, 'REJECT', 1, '平台管理员', 'PENDING', 'REJECTED', '无法提供正规购买凭证，驳回', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 'ORDER', 1005, 'CANCEL', 1, '平台管理员', 'PENDING_PAYMENT', 'CANCELLED', '买家申请取消，管理员确认', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 'PRODUCT', 1, 'SUBMIT', 2, '林晓', NULL, 'PENDING', '卖家提交商品，等待审核', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(8, 'REPORT', 5, 'PROCESS', 1, '平台管理员', 'PENDING', 'PROCESSING', '已联系双方补充凭证', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(9, 'USER', 7, 'WARN', 1, '平台管理员', 'ACTIVE', 'ACTIVE', '因交易纠纷扣减信用分 5 分', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(10, 'PRODUCT', 12, 'APPROVE', 1, '平台管理员', 'PENDING', 'APPROVED', '商品信息完整，予以通过', DATE_SUB(NOW(), INTERVAL 3 DAY));
