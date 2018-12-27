package com.definesys.angrypecker.util.user.userTask;

import com.definesys.angrypecker.util.shiro.ShiroKit;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        String email = "156113@qq.com";
        Map map = new HashMap();
        map.put("loginEmail",email);
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "demo")
                .setClaims(map)
                ;
        String currPassword = ShiroKit.md5(email,"my4");
        System.out.println(currPassword);
        System.out.println(jwtBuilder.compact());
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbkVtYWlsIjoiMTU2MTEzQHFxLmNvbSJ9.yhlXhtoPKzoEZn_-7pRb4H2IflmIAMxb-Ub0eo5TCG5G0ZJLZ99j2P7FCtOYKfG9Y8nlkHvNnLn3XmYhMoP6lA";
        Claims body = Jwts.parser()
                .setSigningKey("demo")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(body.get("loginEmail"));


    }

}
