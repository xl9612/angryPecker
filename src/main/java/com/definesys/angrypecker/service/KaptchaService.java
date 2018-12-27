package com.definesys.angrypecker.service;//package com.definesys.dragon.service;
//
//import com.definesys.mpaas.common.exception.MpaasBusinessException;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.stereotype.Service;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class KaptchaService {
//    private final int field = Calendar.MINUTE;
//    private final int expireTime = 1;
//    private final String key = "captcha";
//
//    public Map<String, String> createToken(String text) {
//        //当前时间的Calendar类型
//        Calendar nowCal = Calendar.getInstance();
//        //将当前时间转到过期时间
//        nowCal.add(field, expireTime);
//        //转化为Date类型
//        Date time = nowCal.getTime();
//        //将字符验证码存储到claims中
//        Claims claims = Jwts.claims();
//        claims.put("text", text);
//        //生成token
//        String token = Jwts.builder().setClaims(claims).setIssuedAt(new Date()).setExpiration(time)
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//        System.out.println("token:" + token);
//        Map<String, String> map = new HashMap<>();
//        map.put("token", token);
//        map.put("test",expireTime+"");
//        return map;
//    }
//
//    public void validateCaptcha(String token, String captcha) {
//        Jws<Claims> jws = null;
//        try {
//            jws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
//        } catch (Exception e) {
//            throw new MpaasBusinessException("验证码错误");
//        }
//        Object text = jws.getBody().get("text");
//        if (!captcha.equals(text))
//            throw new MpaasBusinessException("验证码错误");
//
//    }
//}
