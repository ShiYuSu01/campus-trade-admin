-- 部署后的数据级验证脚本（只读，不修改数据）
-- 用法：mysql -uroot -p campus_trade < sql/verify.sql

USE campus_trade;

SELECT '表数量' AS item, COUNT(*) AS value
FROM information_schema.tables WHERE table_schema = 'campus_trade';

SELECT '用户数' AS item, COUNT(*) AS value FROM t_user
UNION ALL SELECT '商品数', COUNT(*) FROM t_product
UNION ALL SELECT '分类数', COUNT(*) FROM t_category
UNION ALL SELECT '订单数', COUNT(*) FROM t_order
UNION ALL SELECT '举报数', COUNT(*) FROM t_report
UNION ALL SELECT '审计日志数', COUNT(*) FROM t_audit_log;

-- 管理员账号是否存在（不输出任何口令摘要）
SELECT id, username, role, status
FROM t_user WHERE role = 'ADMIN' LIMIT 5;

-- 待审核商品（对应后台“商品审核”角标）
SELECT COUNT(*) AS pending_products FROM t_product WHERE status = 'PENDING';

-- 示例：订单状态分布
SELECT status, COUNT(*) AS cnt FROM t_order GROUP BY status ORDER BY cnt DESC;
