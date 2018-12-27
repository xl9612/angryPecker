package com.definesys.angrypecker.filter;

import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.pojo.FndJwtToken;
import com.definesys.angrypecker.util.common.DragonJwtTokenUtils;
import com.definesys.angrypecker.util.common.ValidateUtils;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Token过滤器
 *
 * @author hackyo
 * Created on 2017/12/8 9:28.
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;
    private DragonJwtTokenUtils jwtTokenUtil;

    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @Autowired
    public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService, DragonJwtTokenUtils jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String uri = request.getRequestURI();
        logger.info("请求地址:"+uri);
        String tokenHead = "Bearer ";
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(tokenHead.length());
            FndJwtToken myToken = sw.buildQuery().eq("jwt_key",authToken).doQueryFirst(FndJwtToken.class);
            if (myToken != null && !ValidateUtils.checkIsNull(myToken.getJwtToken())){
                String rowId = jwtTokenUtil.getUsernameFromToken(myToken.getJwtToken());

                if (rowId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if ("/api/user/logout".equals(uri)){
                        request.setAttribute("token",myToken.getKey());
                    }
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(rowId);
                    if (jwtTokenUtil.validateToken(authToken, (DragonUser) userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }else {
                    sw.buildQuery().bind(FndJwtToken.class).eq("jwt_key",myToken.getJwtToken()).doDelete();
                }
            }

        }
        chain.doFilter(request, response);
    }

}