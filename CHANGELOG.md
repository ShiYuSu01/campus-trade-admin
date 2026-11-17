# CHANGELOG

本项目的变更记录。提交信息遵循 `type: 摘要` 的约定（feat / fix / docs / chore / test）。

## 2026-11-17

### Added
- `HealthServlet`（`GET /health`）：返回应用与数据库连通性 JSON，便于部署后一条命令验收。
- `HealthStatus` DTO：固定字段的轻量返回体，不引入 JSON 库。
- `scripts/verify-deploy.ps1`：健康检查、登录页、登录、后台看板四项一键验证。
- `scripts/health-check.ps1`：只查 `/health` 的巡检脚本，退出码可用于流水线。
- `scripts/init-db.ps1`：按顺序执行 `schema.sql` 与 `data.sql` 并打印数据量。
- `sql/verify.sql`：部署后的数据级只读校验（表数量、各表行数、管理员、待审商品、订单状态分布）。
- `DEPLOY.md`：命令速查版部署手册（环境检查、初始化、构建、部署、验证、故障分支）。
- `docs/06-实验对应说明.md`：8 次实验与仓库文件的对应关系。
- `docs/07-环境与版本矩阵.md`：本机实测版本与兼容性说明。
- `docs/08-配对部署验证.md`：配对部署的记录与评审。
- `docs/09-常见问题补充.md`：容器启动失败、JDK 版本不符两例的真实排查。
- `.gitattributes`：统一换行与编码规则，避免 Windows 下脚本被改成 LF。

## 2026-11-03

### Added
- 初始化仓库。
- 上传校园二手交易平台后台管理系统（完整源码、SQL、脚本与文档）：49 个 Java 类、19 个 JSP、6 张表。
