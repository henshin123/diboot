package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.shiro.BaseJwtAuthenticationToken;
import com.diboot.shiro.config.AuthType;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.service.AuthWayService;
import com.diboot.shiro.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * 用户名密码认证实现
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
public class UsernamePasswordAuthWayServiceImpl implements AuthWayService {

    @Autowired
    private SysUserService sysUserService;

    private AuthType authType = AuthType.USERNAME_PASSWORD;

    private BaseJwtAuthenticationToken token;

    @Override
    public AuthType authType() {
        return authType;
    }

    @Override
    public void initByToken(BaseJwtAuthenticationToken token) {
        this.token = token;
    }

    @Override
    public BaseEntity getUser() {
        QueryWrapper<SysUser> query = new QueryWrapper();
        query.lambda()
                .eq(SysUser::getUsername, token.getAccount());
        List<SysUser> userList = sysUserService.getEntityList(query);
        if (V.isEmpty(userList)){
            return null;
        }
        return userList.get(0);
    }

    @Override
    public boolean requirePassword() {
        return authType.isRequirePassword();
    }

    @Override
    public boolean isPasswordMatch() {
        String password = new String(token.getPassword());

        // 构建查询条件
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysUser::getUsername, token.getAccount())
                .eq(SysUser::getPassword, password);

        // 获取单条用户记录
        List<SysUser> userList = sysUserService.getEntityList(queryWrapper);

        return V.notEmpty(userList);
    }

    @Override
    public boolean isPreliminaryVerified() {
        return false;
    }

    @Override
    public Long getExpiresInMinutes() {
        return null;
    }
}
