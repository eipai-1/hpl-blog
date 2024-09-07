package com.hpl.redis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author : rbe
 * @date : 2024/8/14 14:07
 */
@Slf4j
@Component
public class RedisClient {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //todo 线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);


    /**
     * 将对象转换为JSON字符串并存储到Redis中
     *
     * @param key 要存储的键
     * @param value 要存储的值，该对象将被转换为JSON字符串
     * @param time 键值对的过期时间
     * @param unit 时间单位，用于指定过期时间的单位
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public Boolean setIfAbsent(String key, String value, Long time, TimeUnit timeUnit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
    }

    public void del(String key){
        stringRedisTemplate.delete(key);
    }

    public String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> type) {
    // 1、从redis中查数据
    String json = stringRedisTemplate.opsForValue().get(key);

    // 2、数据不为null 也不等于''
    if (StrUtil.isNotBlank(json)) {
        try {
            // 将数据转换为目标类型并返回
            return JSONUtil.toBean(json, type);
        } catch (Exception e) {
            // 处理转换异常
            log.error("Failed to convert JSON to bean", e);
            return null;
        }
    }

    // 如果 json 为空字符串，则返回 null
    return null;
}


    public <T> List<T> getList(String key, Class<T> type) {
        // 1、从redis中查数据
        String json = stringRedisTemplate.opsForValue().get(key);

        // 2、数据不为null 也不等于''
        if (StrUtil.isNotBlank(json)) {
            // 则将数据转换为目标类型并返回
            JSONArray jsonArray = JSONUtil.parseArray(json);
//            log.warn("jsonArray:{}",jsonArray);
//            log.warn("jsonArray-len:{}",jsonArray.size());
            return jsonArray.toList(type);
        }

        // 3、等于空值 ''
        if(json!=null){
            return Collections.singletonList((T) json);
        }

        return null;
    }

    public Long incr(String key){
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public void incrByStep(String key, Long num){
        stringRedisTemplate.opsForValue().increment(key,num);
    }



//    /**
//     * 根据键前缀和ID查询数据，使用Redis作为缓存，并在缓存未命中时查询数据库
//     * 如果查询到数据，将数据写入Redis缓存；如果未查询到数据，则在Redis中写入空值标记
//     *
//     * @param keyPrefix 键的前缀，用于构建Redis的键
//     * @param id 数据的唯一标识符，用于查询数据和构建Redis的键
//     * @param type 返回的数据类型，用于将JSON字符串转换为目标对象
//     * @param dbFallback 查询数据库的回调函数，用于从数据库中查询数据
//     * @param time 缓存数据的过期时间
//     * @param unit 时间单位，与过期时间配合使用
//     * @return 查询到的数据，或者在未查询到数据时返回null
//     */
//    public <R,ID> R queryWithPassThrough(
//            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit){
//        // 根据前缀和ID构建Redis的键
//        String key = keyPrefix + id;
//        // 从Redis查询数据
//        String json = stringRedisTemplate.opsForValue().get(key);
//        // 判断Redis中是否存在数据
//        if (StrUtil.isNotBlank(json)) {
//            // 如果存在，直接将JSON字符串转换为对应类型的数据并返回
//            return JSONUtil.toBean(json, type);
//        }
//        // 判断命中的是否是空值标记
//        if (json != null) {
//            // 如果是空值标记，返回null表示未找到数据
//            return null;
//        }
//
//        // 如果Redis中没有数据，调用回调函数从数据库中查询
//        R r = dbFallback.apply(id);
//        // 判断数据库中是否查询到数据
//        if (r == null) {
//            // 如果数据库中也未查询到数据，将空值标记写入Redis，并返回null
//            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//            return null;
//        }
//        // 如果数据库中查询到数据，将数据写入Redis缓存
//        this.set(key, r, time, unit);
//        // 返回查询到的数据
//        return r;
//    }


    /**
     * 设置Redis键值对，并添加逻辑过期时间
     * 该方法主要用于缓存击穿的场景，通过在Redis数据中嵌入过期时间，实现过期逻辑
     *
     * @param key 键，唯一标识数据
     * @param value 值，存储的数据对象
     * @param time 过期时间数值，表示数据在Redis中存活的时间
     * @param unit 时间单位，用于将时间数值转换为秒，Redis使用秒作为过期时间的单位
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // 创建RedisData对象，用于封装实际数据和过期时间
        RedisData redisData = new RedisData();
        // 设置存储的数据
        redisData.setData(value);
        // 计算过期时间，当前时间加上指定的过期时间
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // 将RedisData对象转换为JSON字符串，然后写入Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    public <R, ID> R queryWithLogicalExpire(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1.从redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isBlank(json)) {
            // 3.存在，直接返回
            return null;
        }
        // 4.命中，需要先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 5.判断是否过期
        if(expireTime.isAfter(LocalDateTime.now())) {
            // 5.1.未过期，直接返回店铺信息
            return r;
        }
        // 5.2.已过期，需要缓存重建
        // 6.缓存重建
        // 6.1.获取互斥锁
        RedisLock redisLock = new RedisLock(keyPrefix, stringRedisTemplate);
        boolean isLock = redisLock.tryLock(5);
        // 6.2.判断是否获取锁成功
        if (isLock){
            // 6.3.成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 查询数据库
                    R newR = dbFallback.apply(id);
                    // 重建缓存
                    this.setWithLogicalExpire(key, newR, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    // 释放锁
                    redisLock.unlock();
                }
            });
        }
        // 6.4.返回过期的商铺信息
        return r;
    }

    public <R, ID> R queryWithMutex(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1.从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3.存在，直接返回
            return JSONUtil.toBean(shopJson, type);
        }
        // 判断命中的是否是空值
        if (shopJson != null) {
            // 返回一个错误信息
            return null;
        }

        // 4.实现缓存重建
        // 4.1.获取互斥锁
        RedisLock redisLock = new RedisLock(keyPrefix, stringRedisTemplate);
        R r = null;
        try {
            boolean isLock = redisLock.tryLock(5);
            // 4.2.判断是否获取成功
            if (!isLock) {
                // 4.3.获取锁失败，休眠并重试
                Thread.sleep(50);
                return queryWithMutex(keyPrefix, id, type, dbFallback, time, unit);
            }
            // 4.4.获取锁成功，根据id查询数据库
            r = dbFallback.apply(id);
            // 5.不存在，返回错误
            if (r == null) {
                // 将空值写入redis
                stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL(), TimeUnit.SECONDS);
                // 返回错误信息
                return null;
            }
            // 6.存在，写入redis
            this.set(key, r, time, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 7.释放锁
            redisLock.unlock();
        }
        // 8.返回
        return r;
    }


    /********************************
     * 封装set命令
     ********************************/

    public void sAdd(String key, Object value) {
        stringRedisTemplate.opsForSet().add(key, value.toString());
    }

    public void sRem(String key, Object value) {
        stringRedisTemplate.opsForSet().remove(key, value.toString());
    }

    public Boolean sIsMember(String key, Object value) {
        return stringRedisTemplate.opsForSet().isMember(key, value.toString());
    }

    public Set<String> sMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

}