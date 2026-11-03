<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div>
        <h2 class="page-title">审核日志</h2>
        <p class="page-desc">记录商品审核、举报处理、订单流转、用户处罚与分类变更，所有关键操作均可追溯。</p>
    </div>
    <div class="page-head-actions">
        <span class="soft-chip">共 <strong>${pageData.total}</strong> 条记录</span>
    </div>
</section>

<section class="card filter-card">
    <form class="filter-form" method="get" action="${ctx}/admin/audit-logs">
        <label class="filter-field grow">
            <span>关键词</span>
            <input class="input" type="text" name="keyword" value="<c:out value='${keyword}'/>"
                   placeholder="搜索备注或操作人">
        </label>
        <label class="filter-field">
            <span>业务类型</span>
            <select class="select" name="bizType">
                <option value="">全部业务</option>
                <option value="PRODUCT" ${bizType eq 'PRODUCT' ? 'selected' : ''}>商品</option>
                <option value="ORDER" ${bizType eq 'ORDER' ? 'selected' : ''}>订单</option>
                <option value="REPORT" ${bizType eq 'REPORT' ? 'selected' : ''}>举报</option>
                <option value="USER" ${bizType eq 'USER' ? 'selected' : ''}>用户</option>
                <option value="CATEGORY" ${bizType eq 'CATEGORY' ? 'selected' : ''}>分类</option>
            </select>
        </label>
        <label class="filter-field">
            <span>操作动作</span>
            <select class="select" name="logAction">
                <option value="">全部动作</option>
                <option value="APPROVE" ${logAction eq 'APPROVE' ? 'selected' : ''}>审核通过</option>
                <option value="REJECT" ${logAction eq 'REJECT' ? 'selected' : ''}>审核驳回</option>
                <option value="OFF_SHELF" ${logAction eq 'OFF_SHELF' ? 'selected' : ''}>下架</option>
                <option value="RESTORE" ${logAction eq 'RESTORE' ? 'selected' : ''}>重新上架</option>
                <option value="HANDLE" ${logAction eq 'HANDLE' ? 'selected' : ''}>处理举报</option>
                <option value="CANCEL" ${logAction eq 'CANCEL' ? 'selected' : ''}>取消订单</option>
                <option value="BAN" ${logAction eq 'BAN' ? 'selected' : ''}>封禁用户</option>
                <option value="UNBAN" ${logAction eq 'UNBAN' ? 'selected' : ''}>解封用户</option>
            </select>
        </label>
        <div class="filter-actions">
            <button class="btn primary" type="submit">筛选</button>
            <a class="btn ghost" href="${ctx}/admin/audit-logs">重置</a>
        </div>
    </form>
</section>

<section class="card table-card">
    <div class="table-toolbar">
        <div class="table-toolbar-left">
            <h3 class="card-title">操作流水</h3>
            <span class="table-count">按时间倒序</span>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty pageData.items}">
            <div class="empty-state">
                <span class="empty-icon">⌕</span>
                <strong>没有找到符合条件的日志</strong>
                <p>试试更换业务类型或操作动作。</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table log-table">
                    <thead>
                    <tr>
                        <th>时间</th>
                        <th>业务对象</th>
                        <th>操作</th>
                        <th>操作人</th>
                        <th>状态变化</th>
                        <th>备注</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${pageData.items}" var="log">
                        <tr>
                            <td class="muted nowrap">${log.createTimeText}</td>
                            <td>
                                <span class="tag">${log.bizTypeLabel}</span>
                                <span class="mono">#${log.bizId}</span>
                            </td>
                            <td><strong>${log.actionLabel}</strong></td>
                            <td><c:out value="${log.operatorName}"/></td>
                            <td class="nowrap">
                                <span class="status-change">${log.beforeStatus}</span>
                                <span class="status-arrow">→</span>
                                <span class="status-change after">${log.afterStatus}</span>
                            </td>
                            <td class="log-remark"><c:out value="${log.remark}"/></td>
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
