<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="breadcrumb">
    <a href="${ctx}/admin/orders">订单管理</a>
    <span>/</span>
    <span>${order.orderNo}</span>
</div>

<section class="page-head">
    <div>
        <div class="title-row">
            <h2 class="page-title">订单 ${order.orderNo}</h2>
            <span class="badge ${order.statusClass}">${order.statusLabel}</span>
        </div>
        <p class="page-desc">下单于 ${order.createTimeText} · ${order.deliveryLabel} · 订单金额 ${order.amountText}</p>
    </div>
    <div class="page-head-actions">
        <a class="btn ghost" href="${ctx}/admin/orders">返回列表</a>
    </div>
</section>

<div class="detail-layout">
    <div class="detail-main">
        <section class="card order-product-card">
            <img class="order-product-cover" src="${ctx}${order.productCoverOrDefault}" alt="">
            <div class="order-product-main">
                <span class="eyebrow">ORDER ITEM</span>
                <h3><c:out value="${order.productTitle}"/></h3>
                <p>商品 #${order.productId} · 卖家 <c:out value="${order.sellerName}"/></p>
            </div>
            <div class="order-product-price">
                <span>订单金额</span>
                <strong>${order.amountText}</strong>
            </div>
            <a class="btn ghost sm" href="${ctx}/admin/products?action=detail&id=${order.productId}">查看商品</a>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">交易信息</h3>
                    <p class="card-sub">订单双方、交付方式与时间节点</p>
                </div>
            </div>
            <div class="info-grid">
                <div class="info-item"><span>订单编号</span><strong class="mono">${order.orderNo}</strong></div>
                <div class="info-item"><span>订单状态</span><strong>${order.statusLabel}</strong></div>
                <div class="info-item"><span>交付方式</span><strong>${order.deliveryLabel}</strong></div>
                <div class="info-item"><span>收货地址 / 面交点</span><strong><c:out value="${order.address}"/></strong></div>
                <div class="info-item"><span>买家</span><strong><c:out value="${order.buyerName}"/> · <c:out value="${order.buyerPhone}"/></strong></div>
                <div class="info-item"><span>卖家</span><strong><c:out value="${order.sellerName}"/> · <c:out value="${order.sellerPhone}"/></strong></div>
                <div class="info-item"><span>买家备注</span><strong><c:out value="${order.remark}"/></strong></div>
                <div class="info-item"><span>下单时间</span><strong>${order.createTimeText}</strong></div>
            </div>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">时间线</h3>
                    <p class="card-sub">订单关键节点的系统时间</p>
                </div>
            </div>
            <div class="time-grid">
                <div class="time-item">
                    <span class="time-dot done"></span>
                    <div><small>创建订单</small><strong>${order.createTimeText}</strong></div>
                </div>
                <div class="time-item">
                    <span class="time-dot ${not empty order.payTime ? 'done' : ''}"></span>
                    <div><small>买家付款</small><strong>${order.payTimeText}</strong></div>
                </div>
                <div class="time-item">
                    <span class="time-dot ${not empty order.shipTime ? 'done' : ''}"></span>
                    <div><small>卖家发货 / 交付</small><strong>${order.shipTimeText}</strong></div>
                </div>
                <div class="time-item">
                    <span class="time-dot ${not empty order.finishTime ? 'done' : ''}"></span>
                    <div><small>交易完成</small><strong>${order.finishTimeText}</strong></div>
                </div>
                <c:if test="${not empty order.cancelTime}">
                    <div class="time-item">
                        <span class="time-dot danger"></span>
                        <div><small>取消 / 退款</small><strong>${order.cancelTimeText}</strong></div>
                    </div>
                </c:if>
            </div>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">操作记录</h3>
                    <p class="card-sub">管理员对订单的每一次流转</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty logs}">
                    <div class="empty-state small">暂无管理员操作记录</div>
                </c:when>
                <c:otherwise>
                    <div class="timeline">
                        <c:forEach items="${logs}" var="log">
                            <div class="timeline-item">
                                <span class="timeline-dot"></span>
                                <div class="timeline-body">
                                    <div class="timeline-title">
                                        <strong>${log.actionLabel}</strong>
                                        <span class="tag">${log.beforeStatus} → ${log.afterStatus}</span>
                                        <span class="timeline-time">${log.createTimeText}</span>
                                    </div>
                                    <p><c:out value="${log.remark}"/></p>
                                    <small>操作人：<c:out value="${log.operatorName}"/></small>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>

    <aside class="detail-side">
        <section class="card sticky-card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">流转订单状态</h3>
                    <p class="card-sub">当前：${order.statusLabel}</p>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty nextStatuses}">
                    <div class="status-note">
                        <strong>订单已处于终态</strong>
                        <p>已取消或已退款的订单不能再流转。如需恢复交易，请让买家重新下单。</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <form method="post" action="${ctx}/admin/orders" class="stack-form">
                        <input type="hidden" name="id" value="${order.id}">
                        <label class="field">
                            <span class="field-label">目标状态</span>
                            <select class="select" name="targetStatus" required>
                                <c:forEach items="${nextStatuses}" var="option">
                                    <option value="${option.value}">${option.label}</option>
                                </c:forEach>
                            </select>
                        </label>
                        <label class="field">
                            <span class="field-label">操作备注</span>
                            <textarea class="textarea" name="remark" rows="4"
                                      placeholder="例如：已与买家电话确认，同意退款"></textarea>
                        </label>
                        <button class="btn primary block" type="submit"
                                data-confirm="订单状态更新后不可撤销，确认继续吗？">
                            确认更新状态
                        </button>
                        <p class="hint">状态流转会写入审核日志，请谨慎操作。</p>
                    </form>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="card side-note">
            <h3 class="card-title">订单状态说明</h3>
            <ul class="status-legend">
                <li><span class="badge pending-payment">待付款</span> 买家已下单，尚未支付</li>
                <li><span class="badge paid">待发货</span> 买家已付款，等待卖家交付</li>
                <li><span class="badge shipped">待收货</span> 卖家已发货 / 已面交</li>
                <li><span class="badge completed">已完成</span> 交易结束，计入成交额</li>
                <li><span class="badge refunding">退款中</span> 售后处理中</li>
            </ul>
        </section>
    </aside>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
