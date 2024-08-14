package com.hpl.redis;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/14 14:10
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
