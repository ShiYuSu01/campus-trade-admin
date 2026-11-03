package com.campus.trade.service;

import com.campus.trade.common.BizException;
import com.campus.trade.common.PasswordUtil;
import com.campus.trade.dao.UserDao;
import com.campus.trade.entity.User;

/**
 * 登录认证服务。
 */
public class AuthService {

    private final UserDao userDao = new UserDao();

    public User loginAdmin(String username, String password) throws Exception {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BizException("请输入账号和密码");
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            throw new BizException("账号或密码错误");
        }
        if ("BANNED".equals(user.getStatus())) {
            throw new BizException("该账号已被封禁，无法登录后台");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new BizException("该账号不是管理员，不能登录后台");
        }
        userDao.touchLogin(user.getId());
        user.setPassword(null);
        return user;
    }
}
