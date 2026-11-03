<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div>
        <h2 class="page-title">分类管理</h2>
        <p class="page-desc">维护商品分类与排序。停用分类不会影响已发布商品，但前台不再展示该分类入口。</p>
    </div>
    <div class="page-head-actions">
        <span class="soft-chip">共 <strong>${fn:length(categories)}</strong> 个分类</span>
    </div>
</section>

<div class="category-layout">
    <section class="card table-card">
        <div class="table-toolbar">
            <div class="table-toolbar-left">
                <h3 class="card-title">分类列表</h3>
                <span class="table-count">按排序值升序</span>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty categories}">
                <div class="empty-state">
                    <span class="empty-icon">⌕</span>
                    <strong>还没有商品分类</strong>
                    <p>在右侧表单中新增第一个分类。</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="data-table category-table">
                        <thead>
                        <tr>
                            <th>分类</th>
                            <th>商品数</th>
                            <th>排序</th>
                            <th>状态</th>
                            <th>创建时间</th>
                            <th class="actions-col">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${categories}" var="cat">
                            <tr>
                                <td>
                                    <div class="category-cell">
                                        <span class="category-icon">${cat.icon}</span>
                                        <div class="cell-main">
                                            <strong><c:out value="${cat.name}"/></strong>
                                            <span class="cell-sub">#${cat.id}</span>
                                        </div>
                                    </div>
                                </td>
                                <td><span class="soft-chip">${cat.productCount} 件</span></td>
                                <td class="mono">${cat.sort}</td>
                                <td><span class="badge ${cat.statusClass}">${cat.statusLabel}</span></td>
                                <td class="muted">${cat.createTimeText}</td>
                                <td class="actions-col">
                                    <div class="table-actions">
                                        <a class="btn ghost xs"
                                           href="${ctx}/admin/categories?action=edit&id=${cat.id}">编辑</a>
                                        <form method="post" action="${ctx}/admin/categories">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button class="btn ghost xs" type="submit">
                                                ${cat.enabled ? '停用' : '启用'}
                                            </button>
                                        </form>
                                        <form method="post" action="${ctx}/admin/categories">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button class="btn danger-ghost xs" type="submit"
                                                    data-confirm="确定删除分类「<c:out value='${cat.name}'/>」吗？分类下有商品时无法删除。">
                                                删除
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <aside class="detail-side">
        <section class="card sticky-card">
            <div class="card-head">
                <div>
                    <h3 class="card-title">${empty editCategory ? '新增分类' : '编辑分类'}</h3>
                    <p class="card-sub">${empty editCategory ? '创建一个新的商品分类' : '修改分类名称、图标与排序'}</p>
                </div>
            </div>

            <form method="post" action="${ctx}/admin/categories" class="stack-form">
                <input type="hidden" name="action" value="save">
                <c:if test="${not empty editCategory}">
                    <input type="hidden" name="id" value="${editCategory.id}">
                </c:if>

                <label class="field">
                    <span class="field-label">分类名称 <em>必填</em></span>
                    <input class="input" type="text" name="name" maxlength="50" required
                           value="<c:out value='${editCategory.name}'/>"
                           placeholder="例如：数码电子">
                </label>

                <div class="form-grid two">
                    <label class="field">
                        <span class="field-label">图标</span>
                        <input class="input" type="text" name="icon" maxlength="4"
                               value="<c:out value='${empty editCategory ? "📦" : editCategory.icon}'/>"
                               placeholder="📦">
                    </label>
                    <label class="field">
                        <span class="field-label">排序值</span>
                        <input class="input" type="number" name="sort" min="0" max="999"
                               value="${empty editCategory ? 0 : editCategory.sort}">
                    </label>
                </div>

                <div class="form-actions">
                    <button class="btn primary" type="submit">保存分类</button>
                    <c:if test="${not empty editCategory}">
                        <a class="btn ghost" href="${ctx}/admin/categories">取消编辑</a>
                    </c:if>
                </div>
                <p class="hint">排序值越小越靠前。分类名称不能重复。</p>
            </form>
        </section>

        <section class="card side-note">
            <h3 class="card-title">操作提示</h3>
            <ul class="check-list">
                <li>新增分类默认启用</li>
                <li>停用后不影响已发布商品，但前台不再展示入口</li>
                <li>删除前需先把该分类下的商品转移到其他分类</li>
                <li>所有变更都会写入审核日志</li>
            </ul>
        </section>
    </aside>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
