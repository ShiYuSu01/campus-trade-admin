<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:url var="exportUrl" value="/admin/reports">
    <c:param name="action" value="export"/>
    <c:param name="keyword" value="${keyword}"/>
    <c:param name="status" value="${status}"/>
    <c:param name="targetType" value="${targetType}"/>
</c:url>

<section class="page-head">
    <div>
        <h2 class="page-title">举报处理</h2>
        <p class="page-desc">核实举报内容，处理时可联动下架商品、封禁用户或扣减信用分，并自动留痕。</p>
    </div>
    <div class="page-head-actions">
        <span class="soft-chip">待处理 <strong>${navPendingReports}</strong></span>
        <a class="btn ghost" href="${exportUrl}">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                 stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 3v12M7 10l5 5 5-5"/>
                <path d="M5 21h14"/>
            </svg>
            导出 Excel
        </a>
    </div>
</section>

<section class="card filter-card">
    <form class="filter-form" method="get" action="${ctx}/admin/reports">
        <input type="hidden" name="action" value="list">
        <label class="filter-field grow">
            <span>关键词</span>
            <input class="input" type="text" name="keyword" value="<c:out value='${keyword}'/>"
                   placeholder="搜索举报描述或举报人昵称">
        </label>
        <label class="filter-field">
            <span>处理状态</span>
            <select class="select" name="status">
                <option value="">全部状态</option>
                <option value="PENDING" ${status eq 'PENDING' ? 'selected' : ''}>待处理</option>
                <option value="PROCESSING" ${status eq 'PROCESSING' ? 'selected' : ''}>处理中</option>
                <option value="RESOLVED" ${status eq 'RESOLVED' ? 'selected' : ''}>已解决</option>
                <option value="REJECTED" ${status eq 'REJECTED' ? 'selected' : ''}>已驳回</option>
            </select>
        </label>
        <label class="filter-field">
            <span>对象类型</span>
            <select class="select" name="targetType">
                <option value="">全部对象</option>
                <option value="PRODUCT" ${targetType eq 'PRODUCT' ? 'selected' : ''}>商品</option>
                <option value="ORDER" ${targetType eq 'ORDER' ? 'selected' : ''}>订单</option>
                <option value="USER" ${targetType eq 'USER' ? 'selected' : ''}>用户</option>
            </select>
        </label>
        <div class="filter-actions">
            <button class="btn primary" type="submit">筛选</button>
            <a class="btn ghost" href="${ctx}/admin/reports">重置</a>
        </div>
    </form>
</section>

<section class="card table-card">
    <div class="table-toolbar">
        <div class="table-toolbar-left">
            <h3 class="card-title">举报列表</h3>
            <span class="table-count">共 ${pageData.total} 条</span>
        </div>
        <div class="table-toolbar-right">
            <span class="soft-chip">按举报时间倒序</span>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty pageData.items}">
            <div class="empty-state">
                <span class="empty-icon">⌕</span>
                <strong>没有找到符合条件的举报</strong>
                <p>试试更换关键词或筛选条件。</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table report-table">
                    <thead>
                    <tr>
                        <th>编号</th>
                        <th>被举报对象</th>
                        <th>举报原因</th>
                        <th>举报人</th>
                        <th>状态</th>
                        <th>举报时间</th>
                        <th class="actions-col">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${pageData.items}" var="r">
                        <tr>
                            <td><span class="mono">#${r.id}</span></td>
                            <td>
                                <div class="cell-main">
                                    <a class="row-title"
                                       href="${ctx}/admin/reports?action=detail&id=${r.id}">
                                        <c:out value="${r.targetTitle}"/>
                                    </a>
                                    <span class="cell-sub">
                                        <span class="tag">${r.targetTypeLabel}</span>
                                        对象 #${r.targetId}
                                    </span>
                                </div>
                            </td>
                            <td>
                                <strong class="reason-label">${r.reasonLabel}</strong>
                                <span class="cell-sub"><c:out value="${r.description}"/></span>
                            </td>
                            <td>
                                <div class="user-cell">
                                    <span class="user-avatar xs"><c:out value="${fn:substring(r.reporterName, 0, 1)}"/></span>
                                    <c:out value="${r.reporterName}"/>
                                </div>
                            </td>
                            <td><span class="badge ${r.statusClass}">${r.statusLabel}</span></td>
                            <td class="muted">${r.createTimeText}</td>
                            <td class="actions-col">
                                <a class="btn ${r.pending ? 'primary' : 'ghost'} xs"
                                   href="${ctx}/admin/reports?action=detail&id=${r.id}">
                                    ${r.pending ? '处理' : '详情'}
                                </a>
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
