package com.campus.trade.service;

import java.util.List;

import com.campus.trade.common.BizException;
import com.campus.trade.common.DbUtil;
import com.campus.trade.dao.AuditLogDao;
import com.campus.trade.dao.CategoryDao;
import com.campus.trade.entity.AuditLog;
import com.campus.trade.entity.Category;
import com.campus.trade.entity.User;

/**
 * 商品分类管理服务。
 */
public class CategoryService {

    private final CategoryDao categoryDao = new CategoryDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public List<Category> findAll() throws Exception {
        return categoryDao.findAll();
    }

    public List<Category> findEnabled() throws Exception {
        return categoryDao.findEnabled();
    }

    public Category findById(long id) throws Exception {
        Category category = categoryDao.findById(id);
        if (category == null) {
            throw new BizException("分类不存在");
        }
        return category;
    }

    public void save(Long id, String name, String icon, Integer sort, User operator) throws Exception {
        if (name == null || name.isBlank()) {
            throw new BizException("分类名称不能为空");
        }
        if (name.trim().length() > 50) {
            throw new BizException("分类名称不能超过 50 个字符");
        }
        String finalName = name.trim();
        if (finalName.contains("<") || finalName.contains(">")) {
            throw new BizException("分类名称不能包含尖括号等特殊字符");
        }
        String finalIcon = icon == null || icon.isBlank() ? "📦" : icon.trim();
        int finalSort = sort == null ? 0 : sort;
        if (categoryDao.existsName(finalName, id)) {
            throw new BizException("分类名称已存在");
        }

        if (id == null || id <= 0) {
            Category category = new Category();
            category.setName(finalName);
            category.setIcon(finalIcon);
            category.setSort(finalSort);
            category.setStatus("ENABLED");
            DbUtil.inTransaction(connection -> {
                long newId = categoryDao.insert(connection, category);
                auditLogDao.insert(connection, buildLog("CATEGORY", newId, "CREATE",
                        operator, null, "ENABLED", "新增分类：" + finalName));
                return null;
            });
        } else {
            Category category = findById(id);
            category.setName(finalName);
            category.setIcon(finalIcon);
            category.setSort(finalSort);
            DbUtil.inTransaction(connection -> {
                categoryDao.update(connection, category);
                auditLogDao.insert(connection, buildLog("CATEGORY", id, "UPDATE",
                        operator, category.getStatus(), category.getStatus(), "修改分类：" + finalName));
                return null;
            });
        }
    }

    public void toggle(long id, User operator) throws Exception {
        Category category = findById(id);
        String target = category.isEnabled() ? "DISABLED" : "ENABLED";
        DbUtil.inTransaction(connection -> {
            categoryDao.updateStatus(connection, id, target);
            auditLogDao.insert(connection, buildLog("CATEGORY", id, "TOGGLE",
                    operator, category.getStatus(), target,
                    "分类「" + category.getName() + "」" + ("ENABLED".equals(target) ? "启用" : "停用")));
            return null;
        });
    }

    public void delete(long id, User operator) throws Exception {
        Category category = findById(id);
        long productCount = categoryDao.countProducts(id);
        if (productCount > 0) {
            throw new BizException("该分类下还有 " + productCount + " 件商品，不能删除，请先转移商品");
        }
        DbUtil.inTransaction(connection -> {
            categoryDao.delete(connection, id);
            auditLogDao.insert(connection, buildLog("CATEGORY", id, "DELETE",
                    operator, category.getStatus(), null, "删除分类：" + category.getName()));
            return null;
        });
    }

    private AuditLog buildLog(String bizType, long bizId, String action, User operator,
                              String before, String after, String remark) {
        AuditLog log = new AuditLog();
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setAction(action);
        log.setOperatorId(operator.getId());
        log.setOperatorName(operator.getNickname());
        log.setBeforeStatus(before);
        log.setAfterStatus(after);
        log.setRemark(remark);
        return log;
    }
}
