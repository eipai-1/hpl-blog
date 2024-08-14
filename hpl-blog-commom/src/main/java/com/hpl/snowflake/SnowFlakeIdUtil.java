package com.hpl.snowflake;

/**
 * @author : rbe
 * @date : 2024/7/6 9:58
 */
public class SnowFlakeIdUtil {
    /**
     * 默认的id生成器
     */
    public static SnowflakeProducer DEFAULT_ID_PRODUCER = new SnowflakeProducer(new SnowflakeIdGenerator());

    /**
     * 生成全局id
     *
     * @return
     */
    public static Long genId() {
        return DEFAULT_ID_PRODUCER.genId();
    }

    /**
     * 生成字符串格式全局id
     *
     * @return
     */
    public static String genStrId() {
        return CompressUtil.int2str(genId());
    }

}
