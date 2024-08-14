package com.hpl.redis;

import java.util.Random;

/**
 * @author : rbe
 * @date : 2024/8/14 14:57
 */
public class RedisConstants {

    public static final String USER_SESSION_KEY = "user:session:";

    public static final String LOCK_KEY = "lock";

    public static Integer CACHE_NULL_TTL(){

        // 避免缓存穿透，设置过期时间10-15秒
        return new Random().nextInt(10, 16);
    }
}
