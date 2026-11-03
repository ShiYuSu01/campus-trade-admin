<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>无权访问 · 校园二手交易平台</title>
    <link rel="stylesheet" href="${ctx}/assets/css/admin.css">
</head>
<body class="error-body">
<div class="error-page">
    <span class="error-code">403</span>
    <h1>没有权限访问</h1>
    <p>当前账号不是管理员，或账号状态异常。请使用管理员账号重新登录。</p>
    <div class="error-actions">
        <a class="btn primary" href="${ctx}/login">重新登录</a>
        <a class="btn ghost" href="${ctx}/">返回首页</a>
    </div>
</div>
</body>
</html>
