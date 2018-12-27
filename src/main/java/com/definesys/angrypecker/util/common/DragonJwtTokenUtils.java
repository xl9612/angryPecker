package com.definesys.angrypecker.util.common;

import com.definesys.angrypecker.pojo.DragonUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class DragonJwtTokenUtils implements Serializable {

    /**
     * 密钥
     */
    private final String secret = "define";

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @Param s 多少秒
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims,Long s) {
        Date expirationDate = new Date(System.currentTimeMillis() + s * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成令牌
     *
     * @param userDetails 用户
     * @Param s 过久过期(秒)
     * @return 令牌
     */
    public String generateToken(DragonUser userDetails, Long s) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", userDetails.getRowId());
        //claims.put("loginEmail", userDetails.getLoginEmail());
        claims.put("created", new Date());
        claims.put("username", userDetails.getUsername());
        return generateToken(claims,s);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断令牌是否过期
     *true表示过期
     * false表示没有过期
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token,Long s) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("created", new Date());
            refreshedToken = generateToken(claims,s);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param user 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, DragonUser user) {
//        DefineUserDetails user = (DefineUserDetails) userDetails;
        String username = getUsernameFromToken(token);
        return (user.getRowId().equals(username) && !isTokenExpired(token));
    }

}