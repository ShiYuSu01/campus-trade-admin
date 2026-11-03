<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>页面不存在 · 校园二手交易平台</title>
    <link rel="stylesheet" href="${ctx}/assets/css/admin.css">
</head>
<body class="error-body">
<div class="error-page">
    <span class="error-code">404</span>
    <h1>页面走丢了</h1>
    <p>你访问的地址不存在，可能是链接输入错误，或页面已经被移除。</p>
    <div class="error-actions">
        <a class="btn primary" href="${ctx}/admin/dashboard">回到数据看板</a>
        <a class="btn ghost" href="${ctx}/login">登录页</a>
    </div>
</div>
</body>
</html>
