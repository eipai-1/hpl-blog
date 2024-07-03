package com.hpl.util;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/6/30 17:53
 */
public class MapUtil {

    /**
     * 创建并返回一个包含给定键值对的Map。
     * 此方法允许通过一次调用创建一个Map，并初始化一组键值对。第一个键值对通过参数k和v指定，
     * 其余键值对通过可变参数kvs传入，其中每两个连续的元素构成一个键值对。
     *
     * @param k 第一个键，用于初始化Map。
     * @param v 第一个值，与k一起作为第一个键值对初始化Map。
     * @param kvs 其他键值对，以连续的键和值的形式传递。每个键值对由两个连续的元素表示，
     *             其中第一个元素是键，第二个元素是值。
     */
    public static <K, V> Map<K, V> create(K k, V v, Object... kvs) {
        // 根据传入的键值对总数预估Map的大小，以提高性能
        Map<K, V> map = Maps.newHashMapWithExpectedSize(kvs.length + 1);
        // 添加第一个键值对到Map中
        map.put(k, v);
        // 遍历可变参数kvs，每两个元素构成一个键值对，添加到Map中
        for (int i = 0; i < kvs.length; i += 2) {
            map.put((K) kvs[i], (V) kvs[i + 1]);
        }
        return map;
    }


    /**
     * 将集合转换为Map。
     * 通过提供的key和value函数，将集合中的每个元素转换为Map中的一个条目。
     * 如果输入的集合为空，返回一个空的Map。
     *
     * @param list 需要转换的集合。
     * @param key 函数用于从集合元素中提取键。
     * @param val 函数用于从集合元素中提取值。
     * @param <T> 集合中元素的类型。
     * @param <K> Map中键的类型。
     * @param <V> Map中值的类型。
     * @return 转换后的Map。
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> list, Function<T, K> key, Function<T, V> val) {
        // 检查集合是否为空，如果为空，返回一个预期大小为0的空Map，以优化内存分配。
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMapWithExpectedSize(0);
        }
        // 使用提供的key和val函数，将集合流中的每个元素转换为Map中的一个条目。
        return list.stream().collect(Collectors.toMap(key, val));
    }

}
