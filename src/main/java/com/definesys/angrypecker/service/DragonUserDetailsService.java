package com.definesys.angrypecker.service;

import com.definesys.angrypecker.exception.DragonException;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义DragonUserDetailsService
 */
@Component
public class DragonUserDetailsService implements UserDetailsService {

    @Autowired
    private MpaasQueryFactory sw;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建用户信息的逻辑(取数据库/LDAP等用户信息)
        if (ValidateUtils.checkIsNull(username))
            throw new DragonException("登录邮箱不能为空");
        DragonUser dragonUser = sw.buildQuery()
                .addRowIdClause("id","=", username)
                .doQueryFirst(DragonUser.class);
        if (ValidateUtils.checkIsNull(dragonUser+""))
            throw new DragonException("该用户不存在");

        Set authoritiesSet = new HashSet();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authoritiesSet.add(authority);
        dragonUser.setAuthorities(authoritiesSet);
        return dragonUser;
    }

}
