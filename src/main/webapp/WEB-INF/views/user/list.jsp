<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div>
        <h2 class="page-title">用户管理</h2>
        <p class="page-desc">查看平台注册用户、信用分与交易记录，处理封禁与解封。</p>
    </div>
    <div class="page-head-actions">
        <span class="soft-chip">用户总数 <strong>${pageData.total}</strong></span>
    </div>
</section>

<section class="card filter-card">
    <form class="filter-form" method="get" action="${ctx}/admin/users">
        <input type="hidden" name="action" value="list">
        <label class="filter-field grow">
            <span>关键词</span>
            <input class="input" type="text" name="keyword" value="<c:out value='${keyword}'/>"
                   placeholder="搜索账号、昵称或手机号">
        </label>
        <label class="filter-field">
            <span>角色</span>
            <select class="select" name="role">
                <option value="">全部角色</option>
                <option value="USER" ${role eq 'USER' ? 'selected' : ''}>普通用户</option>
                <option value="ADMIN" ${role eq 'ADMIN' ? 'selected' : ''}>管理员</option>
            </select>
        </label>
        <label class="filter-field">
            <span>账号状态</span>
            <select class="select" name="status">
                <option value="">全部状态</option>
                <option value="ACTIVE" ${status eq 'ACTIVE' ? 'selected' : ''}>正常</option>
                <option value="BANNED" ${status eq 'BANNED' ? 'selected' : ''}>已封禁</option>
            </select>
        </label>
        <div class="filter-actions">
            <button class="btn primary" type="submit">筛选</button>
            <a class="btn ghost" href="${ctx}/admin/users">重置</a>
        </div>
    </form>
</section>

<section class="card table-card">
    <div class="table-toolbar">
        <div class="table-toolbar-left">
            <h3 class="card-title">用户列表</h3>
            <span class="table-count">共 ${pageData.total} 人</span>
        </div>
        <div class="table-toolbar-right">
            <span class="soft-chip">按注册时间倒序</span>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty pageData.items}">
            <div class="empty-state">
                <span class="empty-icon">⌕</span>
                <strong>没有找到符合条件的用户</strong>
                <p>试试更换关键词或筛选条件。</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table user-table">
                    <thead>
                    <tr>
                        <th>用户</th>
                        <th>角色</th>
                        <th>校区</th>
                        <th>信用分</th>
                        <th>状态</th>
                        <th>注册时间</th>
                        <th class="actions-col">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${pageData.items}" var="u">
                        <tr>
                            <td>
                                <div class="user-cell">
                                    <span class="user-avatar"><c:out value="${u.initial}"/></span>
                                    <div class="cell-main">
                                        <a class="row-title"
                                           href="${ctx}/admin/users?action=detail&id=${u.id}">
                                            <c:out value="${u.nickname}"/>
                                        </a>
                                        <span class="cell-sub">@<c:out value="${u.username}"/> · #${u.id}</span>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <span class="tag ${u.role eq 'ADMIN' ? 'brand' : ''}">${u.roleLabel}</span>
                            </td>
                            <td class="muted"><c:out value="${u.campus}"/></td>
                            <td><span class="credit ${u.creditClass}">${u.creditScore}</span></td>
                            <td><span class="badge ${u.statusClass}">${u.statusLabel}</span></td>
                            <td class="muted">${u.createTimeText}</td>
                            <td class="actions-col">
                                <a class="btn ghost xs"
                                   href="${ctx}/admin/users?action=detail&id=${u.id}">详情</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/views/common/pagination.jsp" %>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
