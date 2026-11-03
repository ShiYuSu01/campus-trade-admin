/* ============================================================
   校园二手交易平台后台 · 交互脚本
   原生 JavaScript，无 jQuery、无 CDN 依赖
   ============================================================ */
(function () {
    "use strict";

    function ready(fn) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", fn);
        } else {
            fn();
        }
    }

    ready(function () {
        initSidebar();
        initAlerts();
        initConfirms();
        initBatchSelect();
        initRejectCheck();
        initReportForm();
        initLogin();
        initSubmitLock();
    });

    /* ---------- 移动端侧边栏 ---------- */
    function initSidebar() {
        var sidebar = document.getElementById("sidebar");
        var toggle = document.getElementById("menuToggle");
        var mask = document.getElementById("sidebarMask");
        if (!sidebar || !toggle || !mask) {
            return;
        }
        function close() {
            sidebar.classList.remove("open");
            mask.classList.remove("show");
        }
        toggle.addEventListener("click", function () {
            sidebar.classList.toggle("open");
            mask.classList.toggle("show");
        });
        mask.addEventListener("click", close);
        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape") {
                close();
            }
        });
    }

    /* ---------- 提示自动关闭 ---------- */
    function initAlerts() {
        document.querySelectorAll(".alert").forEach(function (alert) {
            var close = alert.querySelector("[data-dismiss]");
            if (close) {
                close.addEventListener("click", function () {
                    dismiss(alert);
                });
            }
            if (alert.hasAttribute("data-auto-dismiss")) {
                window.setTimeout(function () {
                    dismiss(alert);
                }, 6000);
            }
        });
    }

    function dismiss(element) {
        element.style.transition = "opacity .2s, transform .2s";
        element.style.opacity = "0";
        element.style.transform = "translateY(-4px)";
        window.setTimeout(function () {
            element.remove();
        }, 220);
    }

    /* ---------- 二次确认 ---------- */
    function initConfirms() {
        document.querySelectorAll("[data-confirm]").forEach(function (element) {
            element.addEventListener("click", function (event) {
                var message = element.getAttribute("data-confirm");
                if (message && !window.confirm(message)) {
                    event.preventDefault();
                    event.stopPropagation();
                }
            });
        });
    }

    /* ---------- 批量选择 ---------- */
    function initBatchSelect() {
        var selectAll = document.getElementById("selectAll");
        var batchBtn = document.getElementById("batchBtn");
        if (!selectAll || !batchBtn) {
            return;
        }
        var checks = Array.prototype.slice.call(document.querySelectorAll(".row-check"));

        function update() {
            var selected = checks.filter(function (item) {
                return item.checked;
            }).length;
            batchBtn.disabled = selected === 0;
            batchBtn.textContent = selected > 0 ? "批量通过（" + selected + "）" : "批量通过";
            selectAll.checked = checks.length > 0 && selected === checks.length;
            selectAll.indeterminate = selected > 0 && selected < checks.length;
        }

        selectAll.addEventListener("change", function () {
            checks.forEach(function (item) {
                item.checked = selectAll.checked;
            });
            update();
        });
        checks.forEach(function (item) {
            item.addEventListener("change", update);
        });
        update();
    }

    /* ---------- 驳回原因校验 ---------- */
    function initRejectCheck() {
        document.querySelectorAll("[data-reject-check]").forEach(function (button) {
            button.addEventListener("click", function (event) {
                var id = button.getAttribute("data-reject-check");
                var field = document.getElementById(id);
                if (field && !field.value.trim()) {
                    event.preventDefault();
                    field.focus();
                    field.style.borderColor = "#dc2626";
                    field.style.boxShadow = "0 0 0 3px rgba(220,38,38,.1)";
                    window.alert("驳回商品时必须填写驳回原因。");
                }
            });
            var fieldId = button.getAttribute("data-reject-check");
            var field = document.getElementById(fieldId);
            if (field) {
                field.addEventListener("input", function () {
                    field.style.borderColor = "";
                    field.style.boxShadow = "";
                });
            }
        });
    }

    /* ---------- 举报处理联动 ---------- */
    function initReportForm() {
        var form = document.getElementById("handleForm");
        if (!form) {
            return;
        }
        var decision = document.getElementById("decision");
        var action = document.getElementById("handleAction");
        var hint = document.getElementById("actionHint");
        var targetType = form.getAttribute("data-target-type") || "";

        function refresh() {
            var rejected = decision && decision.value === "REJECTED";
            if (action) {
                action.disabled = rejected;
                if (rejected) {
                    action.value = "NONE";
                }
            }
            if (!hint) {
                return;
            }
            if (rejected) {
                hint.textContent = "选择“举报不成立”时不会执行任何附加处罚，仅记录处理说明。";
                hint.className = "callout info compact";
                return;
            }
            var value = action ? action.value : "NONE";
            if (value === "OFF_SHELF") {
                hint.textContent = targetType === "USER"
                    ? "注意：用户举报不能直接下架商品，请选择封禁或警告。"
                    : "提交后将把被举报商品状态改为“已下架”，并写入商品审核日志。";
                hint.className = targetType === "USER" ? "callout danger compact" : "callout info compact";
            } else if (value === "BAN_USER") {
                hint.textContent = "提交后将封禁被举报用户（商品举报封禁卖家，订单举报封禁卖家）。";
                hint.className = "callout danger compact";
            } else if (value === "WARN") {
                hint.textContent = "提交后将扣减该用户 5 分信用分，不会封禁账号。";
                hint.className = "callout info compact";
            } else {
                hint.textContent = "仅处理举报本身，不额外处罚对象。";
                hint.className = "callout info compact";
            }
        }

        if (decision) {
            decision.addEventListener("change", refresh);
        }
        if (action) {
            action.addEventListener("change", refresh);
        }
        refresh();
    }

    /* ---------- 登录页 ---------- */
    function initLogin() {
        var fill = document.getElementById("fillDemo");
        if (fill) {
            fill.addEventListener("click", function () {
                var username = document.querySelector('input[name="username"]');
                var password = document.querySelector('input[name="password"]');
                if (username) {
                    username.value = "admin";
                }
                if (password) {
                    password.value = "123456";
                }
                if (password) {
                    password.focus();
                }
            });
        }
        var show = document.getElementById("showPassword");
        if (show) {
            show.addEventListener("change", function () {
                var password = document.getElementById("password");
                if (password) {
                    password.type = show.checked ? "text" : "password";
                }
            });
        }
    }

    /* ---------- 防重复提交 ---------- */
    function initSubmitLock() {
        document.querySelectorAll("form").forEach(function (form) {
            if ((form.getAttribute("method") || "get").toLowerCase() !== "post") {
                return;
            }
            form.addEventListener("submit", function (event) {
                if (form.getAttribute("data-submitting") === "1") {
                    event.preventDefault();
                    return;
                }
                form.setAttribute("data-submitting", "1");
                var button = event.submitter || form.querySelector('button[type="submit"]');
                if (button) {
                    window.setTimeout(function () {
                        button.disabled = true;
                        button.textContent = "处理中…";
                    }, 30);
                }
                window.setTimeout(function () {
                    form.removeAttribute("data-submitting");
                }, 8000);
            });
        });
    }
})();
