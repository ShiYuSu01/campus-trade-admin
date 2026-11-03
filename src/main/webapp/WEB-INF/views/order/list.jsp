<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:url var="exportUrl" value="/admin/orders">
    <c:param name="action" value="export"/>
    <c:param name="keyword" value="${keyword}"/>
    <c:param name="status" value="${status}"/>
</c:url>

<section class="page-head">
    <div>
        <h2 class="page-title">订单管理</h2>
        <p class="page-desc">查看交易全流程，处理发货、完成、取消与退款。管理员操作会写入审核日志。</p>
    </div>
    <div class="page-head-actions">
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
    <form class="filter-form" method="get" action="${ctx}/admin/orders">
        <input type="hidden" name="action" value="list">
        <label class="filter-field grow">
            <span>关键词</span>
            <input class="input" type="text" name="keyword" value="<c:out value='${keyword}'/>"
                   placeholder="订单号、商品标题、买家或卖家昵称">
        </label>
        <label class="filter-field">
            <span>订单状态</span>
            <select class="select" name="status">
                <option value="">全部状态</option>
                <option value="PENDING_PAYMENT" ${status eq 'PENDING_PAYMENT' ? 'selected' : ''}>待付款</option>
                <option value="PAID" ${status eq 'PAID' ? 'selected' : ''}>待发货</option>
                <option value="SHIPPED" ${status eq 'SHIPPED' ? 'selected' : ''}>待收货</option>
                <option value="COMPLETED" ${status eq 'COMPLETED' ? 'selected' : ''}>已完成</option>
                <option value="CANCELLED" ${status eq 'CANCELLED' ? 'selected' : ''}>已取消</option>
                <option value="REFUNDING" ${status eq 'REFUNDING' ? 'selected' : ''}>退款中</option>
                <option value="REFUNDED" ${status eq 'REFUNDED' ? 'selected' : ''}>已退款</option>
            </select>
        </label>
        <div class="filter-actions">
            <button class="btn primary" type="submit">筛选</button>
            <a class="btn ghost" href="${ctx}/admin/orders">重置</a>
        </div>
    </form>
</section>

<section class="card table-card">
    <div class="table-toolbar">
        <div class="table-toolbar-left">
            <h3 class="card-title">订单列表</h3>
            <span class="table-count">共 ${pageData.total} 笔</span>
        </div>
        <div class="table-toolbar-right">
            <span class="soft-chip">按创建时间倒序</span>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty pageData.items}">
            <div class="empty-state">
                <span class="empty-icon">⌕</span>
                <strong>没有找到符合条件的订单</strong>
                <p>试试更换关键词或状态筛选。</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table order-table">
                    <thead>
                    <tr>
                        <th>订单号</th>
                        <th>商品</th>
                        <th>买家 / 卖家</th>
                        <th>金额</th>
                        <th>状态</th>
                        <th>下单时间</th>
                        <th class="actions-col">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${pageData.items}" var="o">
                        <tr>
                            <td>
                                <a class="mono-link"
                                   href="${ctx}/admin/orders?action=detail&id=${o.id}">${o.orderNo}</a>
                                <span class="cell-sub">${o.deliveryLabel}</span>
                            </td>
                            <td>
                                <div class="product-cell">
                                    <img class="thumb sm" src="${ctx}${o.productCoverOrDefault}" alt="">
                                    <div class="cell-main">
                                        <a class="row-title"
                                           href="${ctx}/admin/orders?action=detail&id=${o.id}">
                                            <c:out value="${o.productTitle}"/>
                                        </a>
                                        <span class="cell-sub">商品 #${o.productId}</span>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div class="people-cell">
                                    <span><em>买</em><c:out value="${o.buyerName}"/></span>
                                    <span><em>卖</em><c:out value="${o.sellerName}"/></span>
                                </div>
                            </td>
                            <td><strong class="money">${o.amountText}</strong></td>
                            <td><span class="badge ${o.statusClass}">${o.statusLabel}</span></td>
                            <td class="muted">${o.createTimeText}</td>
                            <td class="actions-col">
                                <a class="btn ghost xs"
                                   href="${ctx}/admin/orders?action=detail&id=${o.id}">详情</a>
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
