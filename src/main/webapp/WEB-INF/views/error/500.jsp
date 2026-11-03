<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统异常 · 校园二手交易平台</title>
    <link rel="stylesheet" href="${ctx}/assets/css/admin.css">
</head>
<body class="error-body">
<div class="error-page">
    <span class="error-code">500</span>
    <h1>系统开小差了</h1>
    <p>服务器处理请求时出现异常。请稍后重试；如果持续出现，请查看 Tomcat 控制台日志。</p>
    <div class="error-actions">
        <a class="btn primary" href="${ctx}/admin/dashboard">回到数据看板</a>
        <a class="btn ghost" href="${ctx}/login">重新登录</a>
    </div>
    <p class="error-tip">常见原因：MySQL 未启动、数据库账号密码不正确、JSP 编译错误。</p>
</div>
</body>
</html>
