<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员登录 · 校园二手交易平台</title>
    <link rel="stylesheet" href="${ctx}/assets/css/admin.css">
</head>
<body class="login-body">
<div class="login-page">
    <section class="login-brand-panel">
        <div class="login-brand-top">
            <span class="brand-mark">循</span>
            <span class="brand-text">
                <strong>校园二手</strong>
                <small>CAMPUS TRADE</small>
            </span>
        </div>

        <div class="login-hero">
            <span class="eyebrow light">ADMIN CONSOLE · 2026</span>
            <h1>让每一件闲置<br>找到新的主人。</h1>
            <p>面向校园的二手交易治理平台，把商品审核、订单流转、举报处理与用户管理收进同一个工作台。</p>
            <ul class="feature-list">
                <li><span>01</span> 商品先审后发，违规内容及时下架</li>
                <li><span>02</span> 订单状态全程留痕，交易纠纷可追溯</li>
                <li><span>03</span> 举报联动处罚，封禁 / 扣分 / 下架一次完成</li>
            </ul>
        </div>

        <div class="login-stats">
            <div><strong>14</strong><span>演示商品</span></div>
            <div><strong>7</strong><span>平台用户</span></div>
            <div><strong>5</strong><span>举报样例</span></div>
        </div>

        <div class="orb orb-one"></div>
        <div class="orb orb-two"></div>
        <div class="grid-texture"></div>
    </section>

    <section class="login-form-panel">
        <div class="login-card">
            <div class="login-card-head">
                <span class="mobile-brand">循</span>
                <span class="eyebrow">WELCOME BACK</span>
                <h2>管理员登录</h2>
                <p>使用平台管理员账号进入运营后台</p>
            </div>

            <c:if test="${param.logout eq '1'}">
                <div class="alert success compact">
                    <span class="alert-icon">✓</span>
                    <span class="alert-text">已安全退出登录</span>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert error compact">
                    <span class="alert-icon">!</span>
                    <span class="alert-text"><c:out value="${error}"/></span>
                </div>
            </c:if>

            <form method="post" action="${ctx}/login" class="login-form">
                <input type="hidden" name="redirect" value="<c:out value='${redirect}'/>">
                <label class="field">
                    <span class="field-label">管理员账号</span>
                    <span class="input-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="12" cy="8" r="3.5"/>
                            <path d="M5 20c.8-4 3.3-6 7-6s6.2 2 7 6"/>
                        </svg>
                        <input type="text" name="username" value="<c:out value='${username}'/>"
                               placeholder="请输入管理员账号" autocomplete="username" autofocus required>
                    </span>
                </label>

                <label class="field">
                    <span class="field-label">登录密码</span>
                    <span class="input-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                             stroke-linecap="round" stroke-linejoin="round">
                            <rect x="4" y="10" width="16" height="11" rx="2"/>
                            <path d="M8 10V7a4 4 0 0 1 8 0v3M12 14v3"/>
                        </svg>
                        <input type="password" name="password" id="password"
                               placeholder="请输入登录密码" autocomplete="current-password" required>
                    </span>
                </label>

                <div class="login-options">
                    <label class="check-label">
                        <input type="checkbox" id="showPassword">
                        <span>显示密码</span>
                    </label>
                    <span class="login-hint">仅限管理员访问</span>
                </div>

                <button type="submit" class="btn primary block lg">
                    进入运营后台
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round">
                        <path d="M5 12h14M13 6l6 6-6 6"/>
                    </svg>
                </button>
            </form>

            <div class="demo-box">
                <div>
                    <span class="demo-label">演示账号</span>
                    <strong>admin</strong>
                    <span class="demo-sep">/</span>
                    <strong>123456</strong>
                </div>
                <button type="button" class="btn ghost sm" id="fillDemo">一键填入</button>
            </div>

            <p class="login-foot">
                登录即表示你同意遵守平台管理规范。所有审核与处罚操作都会写入日志。
            </p>
        </div>
        <p class="login-copyright">校园二手交易平台 · Java EE 课程设计</p>
    </section>
</div>
<script src="${ctx}/assets/js/admin.js"></script>
</body>
</html>
