package com.hpl.producer;

/**
 * @author : rbe
 * @date : 2024/7/6 10:05
 */
public interface IdGenerator {
    /**
     * 生成分布式id
     *
     * @return
     */
    Long nextId();
}
