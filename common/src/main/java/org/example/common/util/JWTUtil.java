package org.example.common.util;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.common.pojo.User;

import java.util.Date;

public class JWTUtil {
    // 统一密钥，建议后续迁移到 Nacos 或环境变量
    private static final String SECRET = "dreamer-music-secret-please-change";
    // 过期时间：24小时
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L;
    public static String sign(String data) {
        Date date;
        date = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withClaim("body", data)
                .withExpiresAt(date)
                .sign(algorithm);
    }
    public static <T> String sign(T data) {
        Date date;
        date = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String jsonStr = JSON.toJSONString(data);
        return JWT.create()
                .withClaim("body", jsonStr)
                .withExpiresAt(date)
                .sign(algorithm);
    }


    public static <T> T getPayload(String token, Class<T> clazz) {
        DecodedJWT jwt = JWT.decode(token);
        String jsonStr = jwt.getClaim("body").asString();
        return JSON.parseObject(jsonStr, clazz);
    }

    public static String getPayload(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("body").asString();
    }

    public static Long getExpireTime(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Long exp = jwt.getExpiresAt().getTime();
        return exp;
    }

    public static boolean verify(String token) {
        if (token == null) {
            return false;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
    public static User getUser(String authorizationHeader){
        // 处理Bearer前缀
        String token = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // 移除"Bearer "前缀
        }
        System.out.println("Extracted Token: " + token);
        boolean auth = JWTUtil.verify(token);
        User user= new User() ;
        if(auth) {
            user = JWTUtil.getPayload(token,User.class);
        }
        return user;
    }

    public static void main(String args[]) {
        long expireTime = 4320000000L;
        String secret = "ZY78C6Sd3v";
//        User user = new User();
//        user.setId(1);user.setUsername("winter");
//        String token = JWTUtil.sign(user);
//        System.out.println(token);
        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJib2R5Ijoie1wiaWRcIjoxLFwidXNlcm5hbWVcIjpcIndpbnRlclwifSIsImV4cCI6MTc1NTkxNDQyNX0.O0JOZeVqqN2P7Jz93QiwKAFLLPOvUuXM-jdHBAtXtVY";
        boolean auth = JWTUtil.verify(token);
        if(auth) {
            User user = JWTUtil.getPayload(token, User.class);
            System.out.println(user);
        }
        System.out.println(auth);
    }
}