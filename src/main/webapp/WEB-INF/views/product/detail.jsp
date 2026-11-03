<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="breadcrumb">
    <a href="${ctx}/admin/products">商品审核</a>
    <span>/</span>
    <span>商品 #${product.id}</span>
</div>

<section class="page-head">
    <div>
        <div class="title-row">
            <h2 class="page-title"><c:out value="${product.title}"/></h2>
            <span class="badge ${product.statusClass}">${product.statusLabel}</span>
        </div>
        <p class="page-desc">
            商品编号 #${product.id} · 发布于 ${product.createTimeText} · 卖家
            <c:out value="${product.sellerName}"/>
        </p>
    </div>
    <div class="page-head-actions">
        <a class="btn ghost" href="${ctx}/admin/products">返回列表</a>
    </div>
</section>

<div class="detail-layout">
    <div class="detail-main">
        <section class="card product-hero">
            <div class="product-gallery">
                <a class="cover-large" href="${ctx}${product.coverOrDefault}" target="_blank">
                    <img src="${ctx}${product.coverOrDefault}" alt="<c:out value='${product.title}'/>">
                </a>
                <c:if test="${fn:length(product.imageList) gt 1}">
                    <div class="gallery-thumbs">
                        <c:forEach items="${product.imageList}" var="img" varStatus="loop">
                            <a href="${ctx}${img}" target="_blank" class="${loop.first ? 'active' : ''}">
                                <img src="${ctx}${img}" alt="商品图 ${loop.index + 1}">
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
            </div>

            <div class="product-summary">
                <div class="price-row">
                    <span class="price-main">${product.priceText}</span>
                    <c:if test="${not empty product.originalPrice}">
                        <span class="price-original">原价 ${product.originalPriceText}</span>
                    </c:if>
                </div>

                <div class="meta-grid">
                    <div><span>分类</span><strong><c:out value="${product.categoryName}"/></strong></div>
                    <div><span>成色</span><strong>${product.conditionLabel}</strong></div>
                    <div><span>交易校区</span><strong>${product.campus}</strong></div>
                    <div><span>浏览 / 收藏</span><strong>${product.viewCount} / ${product.favoriteCount}</strong></div>
                </div>

                <div class="seller-box">
                    <span class="user-avatar"><c:out value="${fn:substring(product.sellerName, 0, 1)}"/></span>
                    <div class="seller-info">
                        <strong><c:out value="${product.sellerName}"/></strong>
                        <small>卖家 ID #${product.sellerId} · 商品订单 ${product.orderCountSafe} 笔</small>
                    </div>
                    <a class="btn ghost sm" href="${ctx}/admin/users?action=detail&id=${product.sellerId}">查看卖家</a>
                </div>
            </div>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">商品描述</h3>
                    <p class="card-sub">卖家提交的原始描述，仅作审核参考</p>
                </div>
            </div>
            <div class="description"><c:out value="${product.description}"/></div>

            <c:if test="${not empty product.rejectReason}">
                <div class="callout danger">
                    <strong>审核 / 下架原因</strong>
                    <p><c:out value="${product.rejectReason}"/></p>
                </div>
            </c:if>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">审核记录</h3>
                    <p class="card-sub">与该商品相关的全部操作</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty logs}">
                    <div class="empty-state small">暂无操作记录</div>
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
                    <h3 class="card-title">审核操作</h3>
                    <p class="card-sub">当前状态：${product.statusLabel}</p>
                </div>
            </div>

            <c:choose>
                <c:when test="${product.pending}">
                    <form method="post" action="${ctx}/admin/products" class="stack-form" id="auditForm">
                        <input type="hidden" name="action" value="audit">
                        <input type="hidden" name="id" value="${product.id}">
                        <label class="field">
                            <span class="field-label">驳回原因 <em>驳回时必填</em></span>
                            <textarea class="textarea" name="reason" id="rejectReason" rows="4"
                                      placeholder="例如：商品描述与图片不一致，请补充实物照片"></textarea>
                        </label>
                        <div class="form-actions">
                            <button class="btn primary" type="submit" name="decision" value="APPROVED"
                                    data-confirm="确认通过这件商品的审核吗？">
                                通过审核
                            </button>
                            <button class="btn danger-ghost" type="submit" name="decision" value="REJECTED"
                                    data-reject-check="rejectReason">
                                驳回
                            </button>
                        </div>
                        <p class="hint">通过后商品将进入前台展示；驳回后卖家可修改后重新提交。</p>
                    </form>
                </c:when>

                <c:when test="${product.approved}">
                    <form method="post" action="${ctx}/admin/products" class="stack-form">
                        <input type="hidden" name="action" value="status">
                        <input type="hidden" name="id" value="${product.id}">
                        <input type="hidden" name="status" value="OFF_SHELF">
                        <label class="field">
                            <span class="field-label">下架原因</span>
                            <textarea class="textarea" name="reason" rows="4"
                                      placeholder="例如：商品信息涉嫌违规，先下架核实"></textarea>
                        </label>
                        <button class="btn danger" type="submit"
                                data-confirm="确认下架这件商品吗？下架后前台不再展示。">
                            下架商品
                        </button>
                        <p class="hint">下架会保留商品和订单数据，可随时重新上架。</p>
                    </form>
                </c:when>

                <c:when test="${product.offShelf or product.rejected}">
                    <form method="post" action="${ctx}/admin/products" class="stack-form">
                        <input type="hidden" name="action" value="status">
                        <input type="hidden" name="id" value="${product.id}">
                        <input type="hidden" name="status" value="APPROVED">
                        <label class="field">
                            <span class="field-label">重新上架说明</span>
                            <textarea class="textarea" name="reason" rows="3"
                                      placeholder="选填：说明复核结果"></textarea>
                        </label>
                        <button class="btn primary" type="submit"
                                data-confirm="确认将这件商品重新上架吗？">
                            重新上架
                        </button>
                    </form>
                </c:when>

                <c:otherwise>
                    <div class="status-note">
                        <strong>该商品已售出</strong>
                        <p>已售出商品不能重新审核或上下架，请通过订单模块处理售后。</p>
                        <c:url var="orderSearchUrl" value="/admin/orders">
                            <c:param name="keyword" value="${product.title}"/>
                        </c:url>
                        <a class="btn ghost sm" href="${orderSearchUrl}">查看相关订单</a>
                    </div>
                </c:otherwise>
            </c:choose>

            <c:if test="${(product.offShelf or product.rejected) and product.orderCountSafe eq 0}">
                <hr class="divider">
                <form method="post" action="${ctx}/admin/products">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${product.id}">
                    <button class="btn danger-ghost block" type="submit"
                            data-confirm="删除后不可恢复，确定删除这件商品吗？">
                        删除商品
                    </button>
                </form>
            </c:if>
        </section>

        <section class="card side-note">
            <h3 class="card-title">审核要点</h3>
            <ul class="check-list">
                <li>图片是否为实物图，是否与描述一致</li>
                <li>是否涉及违禁品、违规电器或假冒商品</li>
                <li>价格是否明显异常，是否存在引流诈骗</li>
                <li>描述中是否包含站外联系方式</li>
            </ul>
        </section>
    </aside>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
