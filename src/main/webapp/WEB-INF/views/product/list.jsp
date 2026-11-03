<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div>
        <h2 class="page-title">商品审核队列</h2>
        <p class="page-desc">先审后发。通过后商品才会进入前台展示，驳回时请填写具体原因。</p>
    </div>
    <div class="page-head-actions">
        <span class="soft-chip">待审核 <strong>${navPendingProducts}</strong></span>
        <a class="btn ghost" href="${ctx}/admin/products">刷新列表</a>
    </div>
</section>

<section class="card filter-card">
    <form class="filter-form" method="get" action="${ctx}/admin/products">
        <input type="hidden" name="action" value="list">
        <label class="filter-field grow">
            <span>关键词</span>
            <input class="input" type="text" name="keyword" value="<c:out value='${keyword}'/>"
                   placeholder="搜索商品标题或描述">
        </label>
        <label class="filter-field">
            <span>状态</span>
            <select class="select" name="status">
                <option value="">全部状态</option>
                <option value="PENDING" ${status eq 'PENDING' ? 'selected' : ''}>待审核</option>
                <option value="APPROVED" ${status eq 'APPROVED' ? 'selected' : ''}>已通过</option>
                <option value="REJECTED" ${status eq 'REJECTED' ? 'selected' : ''}>已驳回</option>
                <option value="OFF_SHELF" ${status eq 'OFF_SHELF' ? 'selected' : ''}>已下架</option>
                <option value="SOLD" ${status eq 'SOLD' ? 'selected' : ''}>已售出</option>
            </select>
        </label>
        <label class="filter-field">
            <span>分类</span>
            <select class="select" name="categoryId">
                <option value="">全部分类</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="${cat.id}" ${categoryId eq cat.id ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
            </select>
        </label>
        <div class="filter-actions">
            <button class="btn primary" type="submit">筛选</button>
            <a class="btn ghost" href="${ctx}/admin/products">重置</a>
        </div>
    </form>
</section>

<form id="batchForm" method="post" action="${ctx}/admin/products">
    <input type="hidden" name="action" value="batch">
    <input type="hidden" name="decision" value="APPROVED">

    <section class="card table-card">
        <div class="table-toolbar">
            <div class="table-toolbar-left">
                <h3 class="card-title">商品列表</h3>
                <span class="table-count">共 ${pageData.total} 件</span>
            </div>
            <div class="table-toolbar-right">
                <button class="btn primary sm" type="submit" id="batchBtn" disabled
                        data-confirm="确定批量通过选中的商品吗？">
                    批量通过
                </button>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty pageData.items}">
                <div class="empty-state">
                    <span class="empty-icon">⌕</span>
                    <strong>没有找到符合条件的商品</strong>
                    <p>试试更换关键词或状态筛选。</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="data-table product-table">
                        <thead>
                        <tr>
                            <th class="check-col">
                                <input type="checkbox" id="selectAll" aria-label="全选">
                            </th>
                            <th>商品</th>
                            <th>分类</th>
                            <th>卖家</th>
                            <th>价格</th>
                            <th>状态</th>
                            <th>发布时间</th>
                            <th class="actions-col">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${pageData.items}" var="p">
                            <tr>
                                <td class="check-col">
                                    <c:if test="${p.pending}">
                                        <input type="checkbox" class="row-check" name="ids" value="${p.id}"
                                               aria-label="选择商品">
                                    </c:if>
                                </td>
                                <td>
                                    <div class="product-cell">
                                        <img class="thumb" src="${ctx}${p.coverOrDefault}" alt="">
                                        <div class="cell-main">
                                            <a class="row-title"
                                               href="${ctx}/admin/products?action=detail&id=${p.id}">
                                                <c:out value="${p.title}"/>
                                            </a>
                                            <span class="cell-sub">
                                                #${p.id} · ${p.conditionLabel} · ${p.campus}
                                            </span>
                                        </div>
                                    </div>
                                </td>
                                <td><span class="tag">${p.categoryName}</span></td>
                                <td>
                                    <div class="user-cell">
                                        <span class="user-avatar xs"><c:out value="${fn:substring(p.sellerName, 0, 1)}"/></span>
                                        <span><c:out value="${p.sellerName}"/></span>
                                    </div>
                                </td>
                                <td>
                                    <strong class="money">${p.priceText}</strong>
                                    <c:if test="${not empty p.originalPrice}">
                                        <span class="price-old">${p.originalPriceText}</span>
                                    </c:if>
                                </td>
                                <td><span class="badge ${p.statusClass}">${p.statusLabel}</span></td>
                                <td class="muted">${p.createTimeText}</td>
                                <td class="actions-col">
                                    <div class="table-actions">
                                        <c:if test="${p.pending}">
                                            <a class="btn primary xs"
                                               href="${ctx}/admin/products?action=detail&id=${p.id}">审核</a>
                                        </c:if>
                                        <a class="btn ghost xs"
                                           href="${ctx}/admin/products?action=detail&id=${p.id}">详情</a>
                                    </div>
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
</form>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
