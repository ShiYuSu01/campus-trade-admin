<c:if test="${pageData.pages > 1}">
    <div class="pagination">
        <span class="page-info">
            共 <strong>${pageData.total}</strong> 条
            <span class="page-dot">·</span>
            第 ${pageData.page} / ${pageData.pages} 页
        </span>
        <div class="page-btns">
            <c:if test="${pageData.hasPrev}">
                <a class="page-btn" href="${pageData.url}&page=${pageData.page - 1}">上一页</a>
            </c:if>
            <c:forEach items="${pageData.pageNumbers}" var="n">
                <a class="page-btn ${n eq pageData.page ? 'active' : ''}"
                   href="${pageData.url}&page=${n}">${n}</a>
            </c:forEach>
            <c:if test="${pageData.hasNext}">
                <a class="page-btn" href="${pageData.url}&page=${pageData.page + 1}">下一页</a>
            </c:if>
        </div>
    </div>
</c:if>
