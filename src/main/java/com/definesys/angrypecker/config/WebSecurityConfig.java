package com.definesys.angrypecker.config;

import com.definesys.angrypecker.filter.EntryPointUnauthorizedHandler;
import com.definesys.angrypecker.filter.JwtAuthenticationTokenFilter;
import com.definesys.angrypecker.filter.MyLogoutSuccessHandler;
import com.definesys.angrypecker.filter.RestAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全模块配置
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    //认证Token过滤器
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    //认证失败
    private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;

    //权限不足
    private RestAccessDeniedHandler restAccessDeniedHandler;

    //退出登录
    private MyLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, EntryPointUnauthorizedHandler entryPointUnauthorizedHandler, RestAccessDeniedHandler restAccessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.entryPointUnauthorizedHandler = entryPointUnauthorizedHandler;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
//        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers( "/resources/**","/static/**").permitAll()
                .antMatchers("/api/user/login",
                        "/api/user/validateLoginEmail",//判断该邮箱是否注册
                        "/api/user/forget/email",//重置密码发邮件
                        "/api/user/validationexpired",//校验忘记密码邮箱时间
                        "/api/user/resetpassword",//重置密码
                        //"/api/user/certificateMail",//认证发邮件
                        "/api/user/validationauthentication",//认证接口
                        "/api/user/modify/mail/auth",
                        "/api/fnd/generateCheckCode",//图片验证码
                        "/getPicture/*",//图片下载
                        "/api/dragon/securityAuthentication",//安全认证
                        "/api/user/register").permitAll()
                .anyRequest().authenticated()
                .and().headers().cacheControl();
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.exceptionHandling().authenticationEntryPoint(entryPointUnauthorizedHandler).accessDeniedHandler(restAccessDeniedHandler);

    }

}