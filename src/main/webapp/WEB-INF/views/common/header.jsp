<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="loginUser" value="${sessionScope.loginUser}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="noindex,nofollow">
    <title><c:out value="${empty pageTitle ? '后台管理' : pageTitle}"/> · 校园二手交易平台</title>
    <link rel="stylesheet" href="${ctx}/assets/css/admin.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <a class="brand" href="${ctx}/admin/dashboard">
            <span class="brand-mark">循</span>
            <span class="brand-text">
                <strong>校园二手</strong>
                <small>TRADE ADMIN</small>
            </span>
        </a>

        <div class="sidebar-scroll">
            <div class="sidebar-section-label">工作台</div>
            <nav class="nav">
                <a class="nav-item ${activeMenu eq 'dashboard' ? 'active' : ''}" href="${ctx}/admin/dashboard">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="3" width="7" height="7" rx="1.5"/>
                            <rect x="14" y="3" width="7" height="7" rx="1.5"/>
                            <rect x="3" y="14" width="7" height="7" rx="1.5"/>
                            <rect x="14" y="14" width="7" height="7" rx="1.5"/>
                        </svg>
                    </span>
                    <span>数据看板</span>
                </a>
            </nav>

            <div class="sidebar-section-label">交易治理</div>
            <nav class="nav">
                <a class="nav-item ${activeMenu eq 'products' ? 'active' : ''}" href="${ctx}/admin/products">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <path d="M21 8.5 12 3 3 8.5v7L12 21l9-5.5v-7Z"/>
                            <path d="m3 8.5 9 5.5 9-5.5M12 14v7"/>
                        </svg>
                    </span>
                    <span>商品审核</span>
                    <c:if test="${navPendingProducts > 0}">
                        <span class="nav-badge">${navPendingProducts}</span>
                    </c:if>
                </a>
                <a class="nav-item ${activeMenu eq 'orders' ? 'active' : ''}" href="${ctx}/admin/orders">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <path d="M6 3h12v18l-3-2-3 2-3-2-3 2V3Z"/>
                            <path d="M9 8h6M9 12h6"/>
                        </svg>
                    </span>
                    <span>订单管理</span>
                </a>
                <a class="nav-item ${activeMenu eq 'reports' ? 'active' : ''}" href="${ctx}/admin/reports">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <path d="M5 21V4M5 4h11l-1.5 4L16 12H5"/>
                        </svg>
                    </span>
                    <span>举报处理</span>
                    <c:if test="${navPendingReports > 0}">
                        <span class="nav-badge danger">${navPendingReports}</span>
                    </c:if>
                </a>
            </nav>

            <div class="sidebar-section-label">基础数据</div>
            <nav class="nav">
                <a class="nav-item ${activeMenu eq 'users' ? 'active' : ''}" href="${ctx}/admin/users">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="9" cy="8" r="3.2"/>
                            <path d="M3.5 20c.6-3.4 2.8-5.2 5.5-5.2s4.9 1.8 5.5 5.2"/>
                            <path d="M16 5.5a3 3 0 0 1 0 5.8M17.5 14.6c2 .6 3.3 2.3 3.7 5.4"/>
                        </svg>
                    </span>
                    <span>用户管理</span>
                </a>
                <a class="nav-item ${activeMenu eq 'categories' ? 'active' : ''}" href="${ctx}/admin/categories">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20.6 13.4 13.4 20.6a2 2 0 0 1-2.8 0L3 13V3h10l7.6 7.6a2 2 0 0 1 0 2.8Z"/>
                            <circle cx="7.5" cy="7.5" r="1.2"/>
                        </svg>
                    </span>
                    <span>分类管理</span>
                </a>
                <a class="nav-item ${activeMenu eq 'logs' ? 'active' : ''}" href="${ctx}/admin/audit-logs">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="12" cy="12" r="8.5"/>
                            <path d="M12 7.5V12l3 2"/>
                        </svg>
                    </span>
                    <span>审核日志</span>
                </a>
            </nav>
        </div>

        <div class="sidebar-foot">
            <div class="side-tip">
                <span class="side-tip-dot"></span>
                <span>系统运行正常</span>
            </div>
            <a class="nav-item logout" href="${ctx}/logout"
               data-confirm="确定要退出后台吗？">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                         stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 4h4a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-4"/>
                        <path d="M10 8l-4 4 4 4M6 12h10"/>
                    </svg>
                </span>
                <span>退出登录</span>
            </a>
        </div>
    </aside>

    <div class="sidebar-mask" id="sidebarMask"></div>

    <div class="main">
        <header class="topbar">
            <button class="icon-btn menu-toggle" id="menuToggle" type="button" aria-label="打开菜单">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round">
                    <path d="M4 7h16M4 12h16M4 17h16"/>
                </svg>
            </button>
            <div class="topbar-title">
                <h1><c:out value="${empty pageTitle ? '后台管理' : pageTitle}"/></h1>
                <p><c:out value="${empty pageSubtitle ? '校园二手交易平台运营后台' : pageSubtitle}"/></p>
            </div>
            <div class="topbar-right">
                <span class="date-chip">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                         stroke-linecap="round">
                        <rect x="3.5" y="5" width="17" height="15" rx="2"/>
                        <path d="M7.5 3v4M16.5 3v4M3.5 10h17"/>
                    </svg>
                    ${todayText}
                </span>
                <div class="user-chip">
                    <span class="avatar"><c:out value="${loginUser.initial}"/></span>
                    <span class="user-chip-text">
                        <strong><c:out value="${loginUser.nickname}"/></strong>
                        <small>超级管理员</small>
                    </span>
                </div>
            </div>
        </header>

        <main class="content">
            <c:if test="${not empty sessionScope.flashMessage}">
                <div class="alert ${sessionScope.flashType}" data-auto-dismiss>
                    <span class="alert-icon">
                        <c:choose>
                            <c:when test="${sessionScope.flashType eq 'success'}">✓</c:when>
                            <c:otherwise>!</c:otherwise>
                        </c:choose>
                    </span>
                    <span class="alert-text"><c:out value="${sessionScope.flashMessage}"/></span>
                    <button type="button" class="alert-close" data-dismiss>×</button>
                </div>
                <c:remove var="flashMessage" scope="session"/>
                <c:remove var="flashType" scope="session"/>
            </c:if>
