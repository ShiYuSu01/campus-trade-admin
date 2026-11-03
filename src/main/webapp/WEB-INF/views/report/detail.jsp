<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="breadcrumb">
    <a href="${ctx}/admin/reports">举报处理</a>
    <span>/</span>
    <span>举报 #${report.id}</span>
</div>

<section class="page-head">
    <div>
        <div class="title-row">
            <h2 class="page-title">举报 #${report.id}</h2>
            <span class="badge ${report.statusClass}">${report.statusLabel}</span>
            <span class="tag">${report.targetTypeLabel}</span>
        </div>
        <p class="page-desc">
            举报人 <c:out value="${report.reporterName}"/> · 提交于 ${report.createTimeText}
            · 原因：${report.reasonLabel}
        </p>
    </div>
    <div class="page-head-actions">
        <a class="btn ghost" href="${ctx}/admin/reports">返回列表</a>
    </div>
</section>

<div class="detail-layout">
    <div class="detail-main">
        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">举报内容</h3>
                    <p class="card-sub">用户提交的原始描述与证据</p>
                </div>
                <span class="badge ${report.statusClass}">${report.statusLabel}</span>
            </div>
            <div class="report-content">
                <div class="report-reason">
                    <span class="eyebrow">REASON</span>
                    <strong>${report.reasonLabel}</strong>
                </div>
                <p class="pre-wrap"><c:out value="${report.description}"/></p>
            </div>

            <div class="evidence-block">
                <h4>证据材料</h4>
                <c:choose>
                    <c:when test="${empty report.evidenceList}">
                        <div class="evidence-empty">举报人未上传图片证据</div>
                    </c:when>
                    <c:otherwise>
                        <div class="evidence-grid">
                            <c:forEach items="${report.evidenceList}" var="img">
                                <a href="${ctx}${img}" target="_blank">
                                    <img src="${ctx}${img}" alt="举报证据">
                                </a>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">被举报对象</h3>
                    <p class="card-sub">对象快照，点击可跳转到对应管理页面</p>
                </div>
                <span class="tag">${targetInfo.type}</span>
            </div>

            <c:choose>
                <c:when test="${targetInfo.exists}">
                    <a class="target-card" href="${ctx}${targetInfo.link}">
                        <span class="target-icon">${targetInfo.type eq '商品' ? '物' : (targetInfo.type eq '订单' ? '单' : '人')}</span>
                        <span class="target-main">
                            <span class="target-top">
                                <strong><c:out value="${targetInfo.title}"/></strong>
                                <span class="badge ${targetInfo.statusClass}">${targetInfo.statusLabel}</span>
                            </span>
                            <small><c:out value="${targetInfo.subtitle}"/></small>
                            <p><c:out value="${targetInfo.description}"/></p>
                        </span>
                        <span class="target-arrow">→</span>
                    </a>
                </c:when>
                <c:otherwise>
                    <div class="status-note">
                        <strong><c:out value="${targetInfo.title}"/></strong>
                        <p><c:out value="${targetInfo.subtitle}"/></p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">处理记录</h3>
                    <p class="card-sub">举报受理与处罚动作的全部日志</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty logs}">
                    <div class="empty-state small">暂无处理记录</div>
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
        <c:choose>
            <c:when test="${report.pending or report.processing}">
                <section class="card sticky-card">
                    <div class="card-head">
                        <div>
                            <h3 class="card-title">处理举报</h3>
                            <p class="card-sub">当前：${report.statusLabel}</p>
                        </div>
                    </div>

                    <c:if test="${report.pending}">
                        <form method="post" action="${ctx}/admin/reports" class="inline-process-form">
                            <input type="hidden" name="action" value="process">
                            <input type="hidden" name="id" value="${report.id}">
                            <input type="hidden" name="result" value="管理员已受理，开始核实举报内容">
                            <button class="btn ghost block sm" type="submit">标记为处理中</button>
                        </form>
                        <hr class="divider">
                    </c:if>

                    <form method="post" action="${ctx}/admin/reports" class="stack-form"
                          id="handleForm" data-target-type="${report.targetType}">
                        <input type="hidden" name="action" value="handle">
                        <input type="hidden" name="id" value="${report.id}">

                        <label class="field">
                            <span class="field-label">处理结论</span>
                            <select class="select" name="decision" id="decision" required>
                                <option value="RESOLVED">举报成立，予以处理</option>
                                <option value="REJECTED">举报不成立，驳回</option>
                            </select>
                        </label>

                        <label class="field">
                            <span class="field-label">附加处罚</span>
                            <select class="select" name="handleAction" id="handleAction">
                                <option value="NONE">无附加处罚</option>
                                <option value="OFF_SHELF">下架被举报商品</option>
                                <option value="BAN_USER">封禁被举报用户</option>
                                <option value="WARN">警告并扣减 5 分信用</option>
                            </select>
                        </label>

                        <label class="field">
                            <span class="field-label">处理说明 <em>必填</em></span>
                            <textarea class="textarea" name="result" id="result" rows="5" required
                                      placeholder="写清楚核实过程与处理依据，例如：经核实商品为违规电器，已下架并提醒卖家。"></textarea>
                        </label>

                        <div class="callout info compact" id="actionHint">
                            处罚动作会同步作用到被举报对象，并写入审核日志。
                        </div>

                        <button class="btn primary block" type="submit"
                                data-confirm="确认提交处理结果吗？提交后不能重复处理。">
                            提交处理结果
                        </button>
                        <p class="hint">若选择“举报不成立”，附加处罚会自动忽略。</p>
                    </form>
                </section>
            </c:when>

            <c:otherwise>
                <section class="card sticky-card">
                    <div class="card-head">
                        <div>
                            <h3 class="card-title">处理结果</h3>
                            <p class="card-sub">该举报已完成处理</p>
                        </div>
                    </div>
                    <div class="result-box ${report.statusClass}">
                        <span class="result-badge">${report.statusLabel}</span>
                        <p><c:out value="${report.handleResult}"/></p>
                        <div class="result-meta">
                            <span>处罚动作：${report.actionLabel}</span>
                            <span>处理人：<c:out value="${report.handlerName}"/></span>
                            <span>处理时间：${report.handledAtText}</span>
                        </div>
                    </div>
                </section>
            </c:otherwise>
        </c:choose>

        <section class="card side-note">
            <h3 class="card-title">处理建议</h3>
            <ul class="check-list">
                <li>先核对商品 / 订单 / 用户的实际状态</li>
                <li>涉及违规商品优先下架，避免继续交易</li>
                <li>辱骂骚扰、多次欺诈可封禁用户</li>
                <li>轻微违规可警告并扣减信用分</li>
            </ul>
        </section>
    </aside>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
