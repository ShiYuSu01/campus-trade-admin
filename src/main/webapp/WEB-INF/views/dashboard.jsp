<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="hero-card">
    <div class="hero-copy">
        <span class="eyebrow light">TODAY'S OVERVIEW</span>
        <h2>欢迎回来，<c:out value="${loginUser.nickname}"/></h2>
        <p>
            今天有 <strong>${data.stats.pendingProductCount}</strong> 件商品等待审核，
            <strong>${data.stats.pendingReportCount}</strong> 条举报等待处理。
            所有操作都会记录到审核日志，便于追溯。
        </p>
        <div class="hero-actions">
            <a class="btn light" href="${ctx}/admin/products?status=PENDING">去审核商品</a>
            <a class="btn ghost-light" href="${ctx}/admin/reports?status=PENDING">处理举报</a>
        </div>
    </div>
    <div class="hero-art" aria-hidden="true">
        <div class="hero-ring"></div>
        <div class="hero-card-float float-one">
            <span class="float-dot green"></span>
            <span>商品审核</span>
            <strong>${data.stats.pendingProductCount}</strong>
        </div>
        <div class="hero-card-float float-two">
            <span class="float-dot amber"></span>
            <span>举报待办</span>
            <strong>${data.stats.pendingReportCount}</strong>
        </div>
        <div class="hero-big-number">${data.stats.resolutionRate}<small>%</small></div>
        <div class="hero-big-label">举报解决率</div>
    </div>
</section>

<section class="stats-grid">
    <article class="stat-card">
        <div class="stat-top">
            <span class="stat-icon teal">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                     stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="9" cy="8" r="3.2"/>
                    <path d="M3.5 20c.6-3.4 2.8-5.2 5.5-5.2s4.9 1.8 5.5 5.2"/>
                    <path d="M16 5.5a3 3 0 0 1 0 5.8M17.5 14.6c2 .6 3.3 2.3 3.7 5.4"/>
                </svg>
            </span>
            <span class="stat-trend up">+${data.stats.todayUserCount} 今日</span>
        </div>
        <div class="stat-value">${data.stats.userCount}</div>
        <div class="stat-label">注册用户</div>
        <div class="stat-foot">平台普通用户总数</div>
    </article>

    <article class="stat-card">
        <div class="stat-top">
            <span class="stat-icon blue">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                     stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 8.5 12 3 3 8.5v7L12 21l9-5.5v-7Z"/>
                    <path d="m3 8.5 9 5.5 9-5.5M12 14v7"/>
                </svg>
            </span>
            <span class="stat-trend">${data.stats.pendingProductCount} 待审</span>
        </div>
        <div class="stat-value">${data.stats.productCount}</div>
        <div class="stat-label">商品总数</div>
        <div class="stat-foot">含在售、待审、下架与已售</div>
    </article>

    <article class="stat-card">
        <div class="stat-top">
            <span class="stat-icon violet">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                     stroke-linecap="round" stroke-linejoin="round">
                    <path d="M6 3h12v18l-3-2-3 2-3-2-3 2V3Z"/>
                    <path d="M9 8h6M9 12h6"/>
                </svg>
            </span>
            <span class="stat-trend up">+${data.stats.todayOrderCount} 今日</span>
        </div>
        <div class="stat-value">${data.stats.orderCount}</div>
        <div class="stat-label">累计订单</div>
        <div class="stat-foot">包含全部订单状态</div>
    </article>

    <article class="stat-card">
        <div class="stat-top">
            <span class="stat-icon amber">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
                     stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 3v18M17 7.5c0-1.7-2.2-3-5-3s-5 1.3-5 3 2.2 3 5 3 5 1.3 5 3-2.2 3-5 3-5-1.3-5-3"/>
                </svg>
            </span>
            <span class="stat-trend">已完成订单</span>
        </div>
        <div class="stat-value stat-value-money">${data.stats.gmvText}</div>
        <div class="stat-label">累计成交额</div>
        <div class="stat-foot">仅统计已完成订单</div>
    </article>
</section>

<section class="alert-grid">
    <a class="alert-card amber" href="${ctx}/admin/products?status=PENDING">
        <span class="alert-card-icon">审</span>
        <span class="alert-card-body">
            <strong>${data.stats.pendingProductCount} 件商品待审核</strong>
            <small>先审后发，避免违规商品进入校园交易</small>
        </span>
        <span class="alert-card-arrow">→</span>
    </a>
    <a class="alert-card red" href="${ctx}/admin/reports?status=PENDING">
        <span class="alert-card-icon">举</span>
        <span class="alert-card-body">
            <strong>${data.stats.pendingReportCount} 条举报待处理</strong>
            <small>核实后可联动下架商品、封禁用户或扣减信用分</small>
        </span>
        <span class="alert-card-arrow">→</span>
    </a>
</section>

<section class="dashboard-grid">
    <article class="card chart-card">
        <div class="card-head">
            <div>
                <h3 class="card-title">近 7 天订单趋势</h3>
                <p class="card-sub">按订单创建时间统计，柱高代表订单量</p>
            </div>
            <span class="card-chip">7 DAYS</span>
        </div>
        <div class="trend-chart">
            <c:forEach items="${data.trend}" var="point">
                <div class="trend-col" title="${point.label}：${point.count} 单，${point.amountText}">
                    <div class="trend-value">${point.count}</div>
                    <div class="trend-bar-wrap">
                        <div class="trend-bar ${point.count eq 0 ? 'empty' : ''}"
                             style="height: ${point.percent}%"></div>
                    </div>
                    <div class="trend-label">${point.label}</div>
                </div>
            </c:forEach>
        </div>
        <div class="chart-foot">
            <span><i class="legend-dot teal"></i> 每日订单量</span>
            <span>近 7 天按日统计</span>
        </div>
    </article>

    <article class="card">
        <div class="card-head">
            <div>
                <h3 class="card-title">商品分类分布</h3>
                <p class="card-sub">按商品数量统计，含全部状态</p>
            </div>
            <a class="card-link" href="${ctx}/admin/categories">管理分类</a>
        </div>
        <div class="category-list">
            <c:choose>
                <c:when test="${empty data.categories}">
                    <div class="empty-state small">暂无分类数据</div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${data.categories}" var="cat">
                        <div class="cat-row">
                            <div class="cat-head">
                                <span class="cat-name"><c:out value="${cat.name}"/></span>
                                <span class="cat-count">${cat.count} 件</span>
                            </div>
                            <div class="progress"><span style="width: ${cat.percent}%"></span></div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </article>
</section>

<section class="dashboard-grid">
    <article class="card">
        <div class="card-head">
            <div>
                <h3 class="card-title">待审核商品</h3>
                <p class="card-sub">按提交时间从早到晚排序</p>
            </div>
            <a class="card-link" href="${ctx}/admin/products?status=PENDING">查看全部</a>
        </div>
        <c:choose>
            <c:when test="${empty data.pendingProducts}">
                <div class="empty-state">
                    <span class="empty-icon">✓</span>
                    <strong>待审核队列已清空</strong>
                    <p>所有商品都已处理，做得不错。</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="mini-list">
                    <c:forEach items="${data.pendingProducts}" var="p">
                        <a class="mini-item" href="${ctx}/admin/products?action=detail&id=${p.id}">
                            <img class="mini-thumb" src="${ctx}${p.coverOrDefault}" alt="">
                            <span class="mini-main">
                                <strong><c:out value="${p.title}"/></strong>
                                <small><c:out value="${p.sellerName}"/> · ${p.categoryName} · ${p.createTimeText}</small>
                            </span>
                            <span class="mini-price">${p.priceText}</span>
                            <span class="badge pending">待审核</span>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </article>

    <article class="card">
        <div class="card-head">
            <div>
                <h3 class="card-title">待处理举报</h3>
                <p class="card-sub">优先处理最近提交的举报</p>
            </div>
            <a class="card-link" href="${ctx}/admin/reports?status=PENDING">查看全部</a>
        </div>
        <c:choose>
            <c:when test="${empty data.pendingReports}">
                <div class="empty-state">
                    <span class="empty-icon">✓</span>
                    <strong>暂无待处理举报</strong>
                    <p>平台交易秩序良好。</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="report-mini-list">
                    <c:forEach items="${data.pendingReports}" var="r">
                        <a class="report-mini" href="${ctx}/admin/reports?action=detail&id=${r.id}">
                            <span class="report-mini-top">
                                <span class="tag">${r.targetTypeLabel}</span>
                                <span class="badge ${r.statusClass}">${r.statusLabel}</span>
                                <span class="report-mini-time">${r.createTimeText}</span>
                            </span>
                            <strong><c:out value="${r.targetTitle}"/></strong>
                            <small>${r.reasonLabel} · 举报人 <c:out value="${r.reporterName}"/></small>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </article>
</section>

<section class="dashboard-grid">
    <article class="card">
        <div class="card-head">
            <div>
                <h3 class="card-title">最新订单</h3>
                <p class="card-sub">最近产生的 6 笔交易</p>
            </div>
            <a class="card-link" href="${ctx}/admin/orders">订单管理</a>
        </div>
        <div class="table-wrap compact">
            <table class="data-table">
                <thead>
                <tr>
                    <th>订单号</th>
                    <th>商品</th>
                    <th>金额</th>
                    <th>状态</th>
                    <th>时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${data.recentOrders}" var="o">
                    <tr>
                        <td><a class="mono-link" href="${ctx}/admin/orders?action=detail&id=${o.id}">${o.orderNo}</a></td>
                        <td><span class="ellipsis" title="<c:out value='${o.productTitle}'/>"><c:out value="${o.productTitle}"/></span></td>
                        <td class="money">${o.amountText}</td>
                        <td><span class="badge ${o.statusClass}">${o.statusLabel}</span></td>
                        <td class="muted">${o.createTimeText}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </article>

    <article class="card">
        <div class="card-head">
            <div>
                <h3 class="card-title">最近操作</h3>
                <p class="card-sub">商品审核、举报处理与订单流转</p>
            </div>
            <a class="card-link" href="${ctx}/admin/audit-logs">全部日志</a>
        </div>
        <div class="timeline compact">
            <c:forEach items="${data.recentLogs}" var="log">
                <div class="timeline-item">
                    <span class="timeline-dot"></span>
                    <div class="timeline-body">
                        <div class="timeline-title">
                            <strong>${log.actionLabel}</strong>
                            <span class="tag">${log.bizTypeLabel} #${log.bizId}</span>
                        </div>
                        <p><c:out value="${log.remark}"/></p>
                        <small><c:out value="${log.operatorName}"/> · ${log.createTimeText}</small>
                    </div>
                </div>
            </c:forEach>
        </div>
    </article>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
