<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="breadcrumb">
    <a href="${ctx}/admin/users">用户管理</a>
    <span>/</span>
    <span><c:out value="${user.nickname}"/></span>
</div>

<section class="page-head">
    <div>
        <div class="title-row">
            <h2 class="page-title"><c:out value="${user.nickname}"/></h2>
            <span class="badge ${user.statusClass}">${user.statusLabel}</span>
            <span class="tag ${user.role eq 'ADMIN' ? 'brand' : ''}">${user.roleLabel}</span>
        </div>
        <p class="page-desc">@<c:out value="${user.username}"/> · 用户 #${user.id} · 注册于 ${user.createTimeText}</p>
    </div>
    <div class="page-head-actions">
        <a class="btn ghost" href="${ctx}/admin/users">返回列表</a>
    </div>
</section>

<div class="detail-layout">
    <div class="detail-main">
        <section class="card profile-card">
            <span class="profile-avatar"><c:out value="${user.initial}"/></span>
            <div class="profile-main">
                <div class="profile-name">
                    <h3><c:out value="${user.nickname}"/></h3>
                    <span class="badge ${user.statusClass}">${user.statusLabel}</span>
                </div>
                <p>@<c:out value="${user.username}"/> · ${user.roleLabel} · <c:out value="${user.campus}"/></p>
                <div class="profile-meta">
                    <span>手机：<c:out value="${user.phone}"/></span>
                    <span>邮箱：<c:out value="${user.email}"/></span>
                    <span>最后登录：${user.lastLoginTimeText}</span>
                </div>
            </div>
            <div class="credit-panel ${user.creditClass}">
                <span>信用分</span>
                <strong>${user.creditScore}</strong>
                <small>满分 100</small>
            </div>
        </section>

        <section class="count-grid">
            <div class="count-card">
                <span class="count-icon blue">物</span>
                <div><strong>${user.productCount}</strong><span>发布商品</span></div>
            </div>
            <div class="count-card">
                <span class="count-icon violet">单</span>
                <div><strong>${user.orderCount}</strong><span>相关订单</span></div>
            </div>
            <div class="count-card">
                <span class="count-icon amber">举</span>
                <div><strong>${user.reportCount}</strong><span>发起举报</span></div>
            </div>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">最近发布的商品</h3>
                    <p class="card-sub">最多展示 8 件</p>
                </div>
                <c:url var="productSearchUrl" value="/admin/products">
                    <c:param name="keyword" value="${user.nickname}"/>
                </c:url>
                <a class="card-link" href="${productSearchUrl}">商品管理</a>
            </div>
            <c:choose>
                <c:when test="${empty detail.products}">
                    <div class="empty-state small">该用户还没有发布商品</div>
                </c:when>
                <c:otherwise>
                    <div class="mini-list">
                        <c:forEach items="${detail.products}" var="p">
                            <a class="mini-item" href="${ctx}/admin/products?action=detail&id=${p.id}">
                                <img class="mini-thumb" src="${ctx}${p.coverOrDefault}" alt="">
                                <span class="mini-main">
                                    <strong><c:out value="${p.title}"/></strong>
                                    <small>${p.categoryName} · ${p.createTimeText}</small>
                                </span>
                                <span class="mini-price">${p.priceText}</span>
                                <span class="badge ${p.statusClass}">${p.statusLabel}</span>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">最近订单</h3>
                    <p class="card-sub">作为买家或卖家参与的交易</p>
                </div>
                <c:url var="orderSearchUrl" value="/admin/orders">
                    <c:param name="keyword" value="${user.nickname}"/>
                </c:url>
                <a class="card-link" href="${orderSearchUrl}">订单管理</a>
            </div>
            <c:choose>
                <c:when test="${empty detail.orders}">
                    <div class="empty-state small">该用户还没有订单记录</div>
                </c:when>
                <c:otherwise>
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
                            <c:forEach items="${detail.orders}" var="o">
                                <tr>
                                    <td><a class="mono-link"
                                           href="${ctx}/admin/orders?action=detail&id=${o.id}">${o.orderNo}</a></td>
                                    <td><span class="ellipsis"><c:out value="${o.productTitle}"/></span></td>
                                    <td class="money">${o.amountText}</td>
                                    <td><span class="badge ${o.statusClass}">${o.statusLabel}</span></td>
                                    <td class="muted">${o.createTimeText}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">举报记录</h3>
                    <p class="card-sub">该用户发起的举报</p>
                </div>
                <a class="card-link" href="${ctx}/admin/reports">举报管理</a>
            </div>
            <c:choose>
                <c:when test="${empty detail.reports}">
                    <div class="empty-state small">该用户还没有发起举报</div>
                </c:when>
                <c:otherwise>
                    <div class="report-mini-list">
                        <c:forEach items="${detail.reports}" var="r">
                            <a class="report-mini" href="${ctx}/admin/reports?action=detail&id=${r.id}">
                                <span class="report-mini-top">
                                    <span class="tag">${r.targetTypeLabel}</span>
                                    <span class="badge ${r.statusClass}">${r.statusLabel}</span>
                                    <span class="report-mini-time">${r.createTimeText}</span>
                                </span>
                                <strong><c:out value="${r.targetTitle}"/></strong>
                                <small>${r.reasonLabel} · <c:out value="${r.description}"/></small>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">账号操作记录</h3>
                    <p class="card-sub">封禁、解封、重置密码等操作</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty logs}">
                    <div class="empty-state small">暂无账号操作记录</div>
                </c:when>
                <c:otherwise>
                    <div class="timeline">
                        <c:forEach items="${logs}" var="log">
                            <div class="timeline-item">
                                <span class="timeline-dot"></span>
                                <div class="timeline-body">
                                    <div class="timeline-title">
                                        <strong>${log.actionLabel}</strong>
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
                    <h3 class="card-title">账号操作</h3>
                    <p class="card-sub">当前状态：${user.statusLabel}</p>
                </div>
            </div>

            <c:choose>
                <c:when test="${user.role eq 'ADMIN'}">
                    <div class="status-note">
                        <strong>管理员账号受保护</strong>
                        <p>不能在这里封禁或重置管理员密码，避免误操作导致后台无法登录。</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${user.status eq 'ACTIVE'}">
                            <form method="post" action="${ctx}/admin/users" class="stack-form">
                                <input type="hidden" name="action" value="status">
                                <input type="hidden" name="id" value="${user.id}">
                                <input type="hidden" name="status" value="BANNED">
                                <label class="field">
                                    <span class="field-label">封禁原因 <em>必填</em></span>
                                    <textarea class="textarea" name="reason" rows="4" required
                                              placeholder="例如：多次发布违规商品，经提醒仍不改正"></textarea>
                                </label>
                                <button class="btn danger block" type="submit"
                                        data-confirm="封禁后该用户将无法登录和交易，确认封禁吗？">
                                    封禁该用户
                                </button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${ctx}/admin/users" class="stack-form">
                                <input type="hidden" name="action" value="status">
                                <input type="hidden" name="id" value="${user.id}">
                                <input type="hidden" name="status" value="ACTIVE">
                                <label class="field">
                                    <span class="field-label">解封说明</span>
                                    <textarea class="textarea" name="reason" rows="3"
                                              placeholder="选填：说明复核结果"></textarea>
                                </label>
                                <button class="btn primary block" type="submit"
                                        data-confirm="确认恢复该用户的账号吗？">
                                    解除封禁
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <hr class="divider">

                    <form method="post" action="${ctx}/admin/users">
                        <input type="hidden" name="action" value="resetPassword">
                        <input type="hidden" name="id" value="${user.id}">
                        <button class="btn ghost block" type="submit"
                                data-confirm="确定将该用户密码重置为 123456 吗？">
                            重置密码为 123456
                        </button>
                    </form>
                    <p class="hint">重置后请提醒用户尽快修改密码。</p>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="card side-note">
            <h3 class="card-title">信用分规则</h3>
            <ul class="check-list">
                <li>初始信用分 100 分，满分 100</li>
                <li>举报成立并选择警告，扣减 5 分</li>
                <li>信用分低于 70 分标记为风险用户</li>
                <li>严重违规可直接封禁账号</li>
            </ul>
        </section>
    </aside>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
