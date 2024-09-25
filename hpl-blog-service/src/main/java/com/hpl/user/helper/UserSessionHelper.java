package com.hpl.user.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hpl.redis.RedisClient;
import com.hpl.util.JsonUtil;
import com.hpl.util.MapUtil;
import com.hpl.util.RedisUtil;
import com.hpl.util.TraceIdUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用jwt来存储用户token，则不需要后端来存储session了
 *
 * @author YiHui
 * @date 2022/12/5
 */
@Slf4j
@Component
public class UserSessionHelper {

    @Resource
    private RedisClient redisClient;

    @Component
    @Data
    @ConfigurationProperties("hpl.jwt")
    public static class JwtProperties {

        /** 签发人  */
        private String issuer;

        /** 密钥 */
        private String secret;

        /** 有效期，毫秒时间戳 */
        private Long expire;
    }

    private final JwtProperties jwtProperties;

    private Algorithm algorithm;
    private JWTVerifier verifier;


    /**
     * 该构造函数初始化了与JWT（JSON Web Tokens）相关的属性和对象，为处理用户会话提供基础。
     * 它使用提供的JWT配置属性来配置JWT的签名算法和验证器。
     *
     * @param jwtProperties JWT配置属性，包含JWT的密钥和发行者等信息。
     */
    public UserSessionHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // 使用JWT配置中的密钥初始化HMAC256签名算法
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        // 基于算法和发行者配置JWT验证器
        verifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }


    public String genSession(Long userId) {
        // 1.生成jwt格式的会话，内部持有有效期，用户信息
        String session = JsonUtil.objToStr(MapUtil.create("s", TraceIdUtil.generate(), "u", userId));
        String token = JWT.create().withIssuer(jwtProperties.getIssuer())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .withPayload(session)
                .sign(algorithm);

        // 2.使用jwt生成的token时，后端可以不存储这个session信息, 完全依赖jwt的信息
        // 但是需要考虑到用户登出，需要主动失效这个token，而jwt本身无状态，所以再这里的redis做一个简单的token -> userId的缓存，用于双重判定
        String key = "token:"+token;
        redisClient.set(key, userId.toString(), jwtProperties.getExpire(), TimeUnit.MILLISECONDS);
        return token;
    }

    public void removeSession(String session) {
        String key = "token:"+session;
        redisClient.del(key);
    }

    /**
     * 根据会话ID获取用户ID。
     * <p>
     * 该方法通过验证JWT令牌来确定会话的有效性，并从中提取用户ID。如果会话无效或已过期，则返回null。
     * 使用Redis来检查会话的有效性，以应对用户登出时及时失效JWT令牌的情况。
     *
     * @param session 会话ID，实际上是一个JWT令牌。
     * @return 用户ID的长整型数值，如果会话无效或不存在，则返回null。
     */
    public Long getUserIdBySession(String session) {
        // 尝试验证JWT令牌的有效性
        // jwt的校验方式，如果token非法或者过期，则直接验签失败
        try {
            // 验证JWT令牌，如果令牌无效，将抛出异常
            DecodedJWT decodedJWT = verifier.verify(session);
            // 解码JWT负载部分
            String pay = new String(Base64.getDecoder().decode(decodedJWT.getPayload()));

            // 从负载中提取用户ID
            // jwt验证通过，获取对应的userId
            String userId = String.valueOf(JsonUtil.strToObj(pay, HashMap.class).get("u"));

            // 从Redis中获取存储的会话信息，用于对比验证
            // 从redis中获取userId，解决用户登出，后台失效jwt token的问题
            String key = "token:"+session;
            String user = redisClient.get(key);
            log.info("redis userId: {}", user);
            // 如果Redis中的会话信息为空，或与JWT中的用户ID不匹配，则返回null
            if (user == null || !Objects.equals(userId, user)) {
                return null;
            }
            // 如果验证通过，返回用户ID
            return Long.valueOf(user);
        } catch (Exception e) {
            // 记录JWT令牌验证失败的日志
            log.info("jwt token校验失败! token: {}, msg: {}", session, e.getMessage());
            // 验证失败，返回null
            return null;
        }
    }

}
