# DEPLOY.md —— 校园二手交易平台后台管理系统 部署指引

> 详细图文版见 `docs/04-部署运行手册.md`；本文是可复制的命令速查，供没有参与开发的人照做。
> 实测环境：Windows 11 + JDK 25.0.4.1 + Tomcat 11.0.22 + MySQL 8.0.46 + Maven 3.9.16。

## 0. 一句话说明

本项目是 Jakarta EE 9+ 的 war 应用，**必须部署在 Tomcat 10 及以上**（Tomcat 9 会因 `jakarta.servlet` 缺失而报
`ClassNotFoundException`）；数据库为 MySQL 8，库名 `campus_trade`。

## 1. 环境检查

```bat
java -version        :: 需要 17+（源码 release=17）
mvn -v               :: 需要 3.8+
mysql --version      :: 需要 8.x
type %CATALINA_HOME%\RELEASE-NOTES | findstr "Apache Tomcat Version"   :: 需要 10.1 / 11.x
```

## 2. 初始化数据库

```bat
mysql -uroot -p < sql\schema.sql
mysql -uroot -p < sql\data.sql
mysql -uroot -p campus_trade < sql\verify.sql    :: 可选，检查数据是否齐全
```

连接参数在 `src/main/resources/db.properties`，也可用环境变量覆盖（推荐）：

| 环境变量 | 含义 |
| --- | --- |
| `CAMPUS_DB_URL` | JDBC URL |
| `CAMPUS_DB_USER` | 数据库账号 |
| `CAMPUS_DB_PASSWORD` | 数据库口令 |

## 3. 构建

```bat
mvn -B clean package
:: 产物 target\campus-trade-admin.war（约 28 MB，含 Druid/DbUtils/POI/JSTL）
```

## 4. 部署与启动

```bat
copy /Y target\campus-trade-admin.war "%CATALINA_BASE%\webapps\"
"%CATALINA_BASE%\bin\catalina.bat" run
```

`bin\setenv.bat` 建议显式固定 JDK，避免容器使用系统里别的 JRE：

```bat
set "JAVA_HOME=D:\Program Files\Java\jdk-25.0.4.1"
set "JRE_HOME="
set "CATALINA_OPTS=-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"
```

## 5. 验证

```bat
curl http://localhost:8080/campus-trade-admin/health
powershell -ExecutionPolicy Bypass -File scripts\verify-deploy.ps1
```

浏览器访问 <http://localhost:8080/campus-trade-admin/>，账号 `admin / 123456`。

局域网访问：把 `localhost` 换成机器内网 IP，并确认防火墙已放行 8080：

```bat
netsh advfirewall firewall add rule name="Tomcat 8080" dir=in action=allow protocol=TCP localport=8080
```

## 6. 故障分支

| 现象 | 原因 | 处理 |
| --- | --- | --- |
| 启动即退出，日志“子容器启动失败” | 容器里残留了别的 Context（如 `server.xml` 里的硬编码 `<Context>`），其依赖缺失 | 删除该 Context 或补齐依赖后重启 |
| `NoClassDefFoundError: jakarta/servlet/http/HttpServlet` | 用了 Tomcat 9 或更低版本 | 换 Tomcat 10+/11 |
| `BindException: Address already in use` | 8080 被占用 | `netstat -ano \| findstr :8080` 找到进程后结束，或改 server.xml 端口 |
| 启动日志 JDK 与预期不符 | `bin/setclasspath.bat` 里硬编码了 `set JRE_HOME=...` | 注释该行，由 `setenv.bat` 统一管理 |
| 页面能开但登录报数据库错误 | `db.properties` 账号/口令不对，或 MySQL 未启动 | 用 `scripts\health-check.ps1` 看 database 字段定位 |
| 中文乱码 | 响应未声明 UTF-8 或库表不是 utf8mb4 | 确认 EncodingFilter 生效、库表字符集为 utf8mb4 |
