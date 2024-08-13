package com.hpl.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author : rbe
 * @date : 2024/7/1 11:05
 */
public class JsonUtil {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();


    /**
     * 将字符串转换为指定类的对象。
     * 使用JSON_MAPPER的readValue方法将字符串解析为指定类型的对象。
     * 如果解析过程中发生异常，则抛出UnsupportedOperationException。
     */
    public static  <T> T strToObj(String str, Class<T> clz) {
        try {
            // 将字符串转换为指定类的对象。
            return JSON_MAPPER.readValue(str, clz);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }


    /**
     * 将给定的对象转换为字符串表示。
     * 使用JSON库将任意类型的对象转换为字符串。这在需要以可读格式表示对象时非常有用，
     * 比如在日志记录或数据传输中。
     */
    public static <T> String objToStr(T t) {
        try {
            // 尝试使用JSON库将对象转换为字符串
            return JSON_MAPPER.writeValueAsString(t);
        } catch (Exception e) {
            // 如果转换失败，抛出UnsupportedOperationException异常
            throw new UnsupportedOperationException(e);
        }
    }


    /**
     * 序列化模块用于将特定类型的对象序列化为JSON时使用字符串表示法。
     * 该方法创建一个SimpleModule，配置了一系列序列化器，
     * 用于将Long、long、BigDecimal、BigInteger及其数组类型序列化为字符串。
     * 这是必要的，因为JSON格式中没有直接对应大整数类型，且JavaScript的数字类型不能精确表示所有Java的long值。
     * 使用字符串表示法可以确保数据的精确性。
     *
     * @return 返回配置了特定序列化器的SimpleModule。
     */
    public static SimpleModule bigIntToStrsimpleModule() {
        // 创建一个新的SimpleModule实例，用于注册自定义序列化器。
        SimpleModule simpleModule = new SimpleModule();

        // 为Long类型注册一个序列化器，将Long对象转换为字符串。
        simpleModule.addSerializer(Long.class, newSerializer(s -> String.valueOf(s)));
        // 为long原始类型注册一个序列化器，使用ToStringSerializer确保long值被转换为字符串。
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // 为long数组类型注册一个序列化器，将long数组转换为字符串数组。
        simpleModule.addSerializer(long[].class, newSerializer((Function<Long, String>) String::valueOf));
        // 为Long数组类型注册一个序列化器，将Long数组转换为字符串数组。
        simpleModule.addSerializer(Long[].class, newSerializer((Function<Long, String>) String::valueOf));

        // 为BigDecimal类型注册一个序列化器，使用toString方法将BigDecimal对象转换为字符串。
        simpleModule.addSerializer(BigDecimal.class, newSerializer(BigDecimal::toString));
        // 为BigDecimal数组类型注册一个序列化器，将BigDecimal数组转换为字符串数组。
        simpleModule.addSerializer(BigDecimal[].class, newSerializer(BigDecimal::toString));

        // 为BigInteger类型注册一个序列化器，使用ToStringSerializer确保BigInteger值被转换为字符串。
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        // 为BigInteger数组类型注册一个序列化器，将BigInteger数组转换为字符串数组。
        simpleModule.addSerializer(BigInteger[].class, newSerializer((Function<BigInteger, String>) BigInteger::toString));

        // 返回配置了自定义序列化器的SimpleModule。
        return simpleModule;
    }


    /**
     * 创建一个自定义的JsonSerializer实例，该实例使用提供的Function将对象转换为字符串。
     * 此方法为泛型方法，允许序列化的对象T和Function的参数类型K是泛型。
     *
     * @param func 一个函数，用于将类型K的对象转换为字符串。这个函数是这个序列化器的核心，它定义了如何将对象转换为字符串。
     * @param <T>  要序列化的对象的类型。
     * @param <K>  函数参数的类型，必须能强制转换为T类型。
     * @return 返回一个JsonSerializer实例，该实例使用提供的函数将对象序列化为字符串。
     */
    public static <T, K> JsonSerializer<T> newSerializer(Function<K, String> func) {
        return new JsonSerializer<T>() {
            /**
             * 序列化对象的方法。这个方法覆盖了JsonSerializer的serialize方法。
             * 它首先检查对象是否为null，如果是，则直接写入一个null值。
             * 如果对象是一个数组，那么它会将每个元素转换为字符串并写入数组中。
             * 如果对象不是一个数组，那么它直接将对象转换为字符串并写入。
             * 在转换过程中，如果发生IOException，将会抛出一个运行时异常。
             *
             * @param t 要序列化的对象。
             * @param jsonGenerator 用于生成JSON的工具。
             * @param serializerProvider 序列化提供者，可以用来获取其他序列化器。
             * @throws IOException 如果在写入JSON过程中发生错误。
             */
            @Override
            public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (t == null) {
                    jsonGenerator.writeNull();
                    return;
                }

                if (t.getClass().isArray()) {
                    jsonGenerator.writeStartArray();
                    Stream.of(t).forEach(s -> {
                        try {
                            jsonGenerator.writeString(func.apply((K) s));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    jsonGenerator.writeEndArray();
                } else {
                    jsonGenerator.writeString(func.apply((K) t));
                }
            }
        };
    }

}