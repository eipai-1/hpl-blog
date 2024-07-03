package com.hpl.util;


import com.google.common.collect.Maps;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.zset.Tuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.BiConsumer;


/**
 * @author : rbe
 * @date : 2024/7/1 11:13
 */
public class RedisUtil {
    private static final Charset CODE = StandardCharsets.UTF_8;
    private static final String KEY_PREFIX = "hyper_";
    private static RedisTemplate<String, String> template;

    public static void registerTemplate(RedisTemplate<String, String> template) {
        RedisUtil.template = template;
    }

    public static void checkNotNull(Object... args) {
        for (Object obj : args) {
            if (obj == null) {
                throw new IllegalArgumentException("redis argument can not be null!");
            }
        }
    }

    /**
     * 将给定的值转换为字节数组。
     * <p>
     * 此方法主要用于将各种类型的值进行序列化，以便于存储或传输。对于字符串类型，直接使用指定编码转换为字节数组；
     * 对于非字符串类型，先通过JSON库将其转换为字符串，然后再进行编码转换。
     * 这种方法的选择使得可以处理多种类型的对象，而不需要对每种类型都实现特定的序列化逻辑。
     */
    public static <T> byte[] valToBytes(T val) {
        // 如果值是字符串类型，直接使用指定编码转换为字节数组
        if (val instanceof String) {
            return ((String) val).getBytes(CODE);
        } else {
            // 如果值不是字符串类型，先将其转换为JSON字符串，然后再转换为字节数组
            return JsonUtil.objToStr(val).getBytes(CODE);
        }
    }


    /**
     * 将给定的键转换为特定编码的字节数组，用于缓存键的生成。
     * 在键前添加了一个固定的前缀，以确保缓存键的唯一性和一致性。
     *
     * @param key 原始键字符串
     * @return 添加前缀后的键的字节数组
     */
    public static byte[] keyToBytes(String key) {
        // 检查键是否为null，如果是，则抛出异常
        checkNotNull(key);
        // 在键前添加前缀，形成缓存中的唯一键
        key = KEY_PREFIX + key;
        // 将形成后的键转换为指定编码的字节数组
        return key.getBytes(CODE);
    }

    public static byte[][] keyToBytes(List<String> keys) {
        byte[][] bytes = new byte[keys.size()][];
        int index = 0;
        for (String key : keys) {
            bytes[index++] = keyToBytes(key);
        }
        return bytes;
    }

    /**
     * 将字节型数组转换为指定类型的对象。
     * 该方法主要用于将从数据库或网络中获取的字节型数据转换为Java对象，
     * 支持字符串和由JsonUtil支持的任意Java类型。
     */
    private static <T> T toObj(byte[] ans, Class<T> clz) {
        // 检查输入的字节型数组是否为null，如果是，则直接返回null
        if (ans == null) {
            return null;
        }

        // 如果指定的类型为String，则直接通过新的字符串构造函数和预定义的编码进行转换
        if (clz == String.class) {
            return (T) new String(ans, CODE);
        }

        // 对于其他类型，先将字节型数组转换为字符串，然后使用JsonUtil的解析方法将其转换为指定类型
        return JsonUtil.strToObj(new String(ans, CODE), clz);
    }


    /***********************************
     *      string（字符串）数据结构api
     ***********************************/


    /**
     * 将给定的键值对存储到缓存中。
     * <p>
     * 此方法通过将键和值转换为字节数组，然后使用Redis模板将它们存储到Redis缓存中。
     * 这种转换允许存储任意类型的键和值，只要它们可以被转换为字节数组。
     */
    public static void set(String key, String value) {
        template.execute((RedisCallback<Void>) con -> {
            con.set(keyToBytes(key), valToBytes(value));
            return null;
        });
    }


    /**
     * 从Redis缓存中获取指定键的值。
     */
    public static String get(String key) {
        return template.execute((RedisCallback<String>) con -> {
            // 将字符串键转换为字节数组，以适应Redis的字节操作。
            byte[] val = con.get(keyToBytes(key));
            // 如果获取的值为空，则返回null；否则将字节数组转换为字符串并返回。
            return val == null ? null : new String(val);
        });
    }


    /**
     * 返回key的有效期
     */
    public static Long ttl(String key) {
        return template.execute((RedisCallback<Long>) con -> con.ttl(keyToBytes(key)));
    }


    /**
     * 设置指定缓存键的过期时间。
     * <p>
     * 通过此方法，可以为一个缓存键设置一个固定的过期时间，以秒为单位。这有助于实现缓存的自动失效机制，避免缓存数据长时间不更新导致的潜在问题。
     * 使用此方法时，需要传入要设置过期时间的缓存键以及过期时间长度。
     *
     * @param key    缓存键，需要设置过期时间的键。
     * @param expire 过期时间，以秒为单位。单位为秒，确保了缓存的过期时间可以精确控制。
     */
    public static void expire(String key, Long expire) {
        // 使用Redis模板执行回调函数，专门处理缓存过期设置
        template.execute((RedisCallback<Void>) connection -> {
            // 将键名转换为字节数组，以适应Redis的操作接口，然后设置过期时间
            connection.expire(keyToBytes(key), expire);
            // 因为expire方法不返回任何有用的结果，所以这里返回null
            return null;
        });
    }


    /**
     * 删除缓存中指定键的条目。
     * 注意：此方法不处理键为空的情况，调用方应确保传入的键不为空。
     */
    public static void del(String key) {
        // 检查键是否为null，如果是，则抛出异常
        checkNotNull(key);
        // 使用模板执行Redis回调，该回调负责删除指定的键。
        template.execute((RedisCallback<Long>) con -> con.del(keyToBytes(key)));
    }


    /**
     * 将字符串键值对存储到缓存中，并设定过期时间。
     * <p>
     * 此方法用于在Redis缓存中设置一个键值对，并为这个键值对指定一个过期时间。
     * 这是很有用的，因为它允许我们缓存数据，同时确保数据不会永久保存在缓存中，
     * 而是会在一段时间后自动删除，从而避免了缓存数据过期不及时的问题。
     */
    public static Boolean setEx(String key, String value, Long expire) {
        // 使用Redis模板执行缓存设置操作。
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                // 将键和值转换为字节数组，并设置过期时间，然后返回操作结果。
                return redisConnection.setEx(keyToBytes(key), expire, valToBytes(value));
            }
        });
    }


    /***********************************
     *      hash（哈希）数据结构api
     ***********************************/


    /**
     * 使用Redis的hSet命令将值设置到哈希表中。
     */
    public static <T> Boolean hSet(String key, String field, T ans) {
        // 使用Redis模板执行操作，将值设置到哈希表中。
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                // 将键值对转换为字节数组并调用redisConnection的hSet方法进行设置。
                return redisConnection.hSet(keyToBytes(key), valToBytes(field), valToBytes(ans));
            }
        });
    }

    /**
     * 该方法通过指定的key和field从Redis的哈希表中获取对应的数据，并将数据转换为Java对象返回。
     * 如果指定的field不存在，则返回null。使用泛型参数T，允许将返回值转换为任何指定的类型。
     */
    public static <T> T hGet(String key, String field, Class<T> clz) {
        // 使用Redis模板执行回调函数，以异步方式从Redis获取数据。
        return template.execute((RedisCallback<T>) con -> {
            // 将key和field转换为字节数组，以适应Redis的字节序列存储方式。
            byte[] records = con.hGet(keyToBytes(key), valToBytes(field));
            // 如果获取的数据为空，则直接返回null。
            if (records == null) {
                return null;
            }
            // 将从Redis获取的字节数组数据转换为指定类型的Java对象。
            return toObj(records, clz);
        });
    }


    /**
     * 从Redis的哈希表中获取所有字段和值。
     * <p>
     * 此方法通过指定的键和类类型，从Redis的哈希表中检索所有字段和值，并将它们转换为Java对象。
     * 使用模板模式执行Redis操作，确保操作的原子性和一致性。
     */
    public static <T> Map<String, T> hGetAll(String key, Class<T> clz) {
        // 执行Redis命令，获取指定键的所有字段和值，结果以byte[]数组的形式返回。
        Map<byte[], byte[]> records = template.execute((RedisCallback<Map<byte[], byte[]>>) con -> con.hGetAll(keyToBytes(key)));

        // 如果哈希表不存在，返回空Map。
        if (records == null) {
            return Collections.emptyMap();
        }

        // 预估结果集大小，创建一个新的Map来存储转换后的字段和值。
        Map<String, T> result = Maps.newHashMapWithExpectedSize(records.size());
        // 遍历结果集，将byte[]类型的键值对转换为字符串和对象类型的键值对。
        for (Map.Entry<byte[], byte[]> entry : records.entrySet()) {
            // 如果键为null，则跳过当前条目。
            if (entry.getKey() == null) {
                continue;
            }

            // 将byte[]类型的键转换为字符串，将byte[]类型的值反序列化为指定类型的对象，并添加到结果Map中。
            result.put(new String(entry.getKey()), toObj(entry.getValue(), clz));
        }
        // 返回转换后的Map。
        return result;
    }

    /**
     * 对Redis哈希表中的指定字段的值进行增量操作。
     * <p>
     * 该方法通过指定的键和字段，对字段的数值进行增加操作。
     * 如果字段不存在，会先创建该字段并将其值设置为0，然后进行增加操作。
     * 此方法适用于需要对哈希表中的数值进行原子性增加的场景。
     */
    public static Long hIncr(String key, String filed, Integer cnt) {
        // 使用Redis模板执行增量操作，将键和字段名转换为字节数组后，对指定字段的值进行增量操作。
        return template.execute((RedisCallback<Long>) con -> con.hIncrBy(keyToBytes(key), valToBytes(filed), cnt));
    }


    /**
     * 从Redis的哈希表中删除指定的字段。
     */
    public static <T> Boolean hDel(String key, String field) {
        // 使用Redis模板执行操作，回调函数定义了具体的删除逻辑。
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                // 将键名和字段名转换为字节数组，以适应Redis的字节操作。
                // 然后调用Redis的hDel命令尝试删除字段，并检查返回值是否大于0，以确认删除是否成功。
                return connection.hDel(keyToBytes(key), valToBytes(field)) > 0;
            }
        });
    }


    /**
     * 使用HashMap批量设置给定键的字段和值。
     * 此方法用于将Map中的字段和值转换为字节数组，然后通过Redis的hMSet命令将它们存储到指定的键中。
     * 通过预先估计Map的大小来创建一个具有适当容量的HashMap，以提高性能。
     */
    public static <T> void hMSet(String key, Map<String, T> fields) {
        // 根据fields的大小预先分配空间，以提高效率
        Map<byte[], byte[]> val = Maps.newHashMapWithExpectedSize(fields.size());
        // 遍历fields中的每个条目，将键和值转换为字节数组，并存储到val中
        for (Map.Entry<String, T> entry : fields.entrySet()) {
            val.put(valToBytes(entry.getKey()), valToBytes(entry.getValue()));
        }
        // 使用Redis模板执行回调函数，将转换后的键值对通过hMSet命令存储到Redis中
        template.execute((RedisCallback<Object>) connection -> {
            connection.hMSet(keyToBytes(key), val);
            return null;
        });
    }


    /**
     * 通过给定的key和fields从Redis的hash中获取多个field的值，并将它们转换为指定类型T。
     * 使用模板模式执行Redis的hMGet操作，将结果从字节序列转换为Java对象。
     */
    public static <T> Map<String, T> hMGet(String key, final List<String> fields, Class<T> clz) {
        // 使用RedisTemplate的execute方法执行回调函数。
        return template.execute(new RedisCallback<Map<String, T>>() {

            // 在Redis连接上执行具体的hMGet操作。
            @Override
            public Map<String, T> doInRedis(RedisConnection connection) throws DataAccessException {

                // 将fields列表转换为byte[][]，因为Redis操作使用byte[]。
                byte[][] f = new byte[fields.size()][];
                IntStream.range(0, fields.size()).forEach(i -> f[i] = valToBytes(fields.get(i)));

                // 调用Redis的hMGet方法获取多个field的值。
                List<byte[]> ans = connection.hMGet(keyToBytes(key), f);

                // 初始化一个Map，用于存储field到value的映射。
                Map<String, T> result = Maps.newHashMapWithExpectedSize(fields.size());

                // 将每个field的值从byte[]转换为指定类型T，并添加到result Map中。
                IntStream.range(0, fields.size()).forEach(i -> {
                    result.put(fields.get(i), toObj(ans.get(i), clz));
                });

                // 返回包含转换后的值的Map。
                return result;
            }
        });
    }


    /***********************************
     *      set（集合）数据结构api
     ***********************************/


    /**
     * 将给定的值添加到指定的Set集合中。
     * 如果成功添加值，则返回1；如果Set集合已包含该值，则返回0；如果操作失败，则返回负数。
     */
    public static <T> boolean sAdd(String key, T val) {
        // 使用RedisCallback回调机制，在Redis模板中执行添加操作。
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                // 将键和值转换为字节数组，然后调用Redis的sAdd命令添加到Set集合中。
                return connection.sAdd(keyToBytes(key), valToBytes(val));
            }
        }) > 0;
    }


    /**
     * 判断value是否再set中
     */
    public static <T> Boolean sIsMember(String key, T value) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.sIsMember(keyToBytes(key), valToBytes(value));
            }
        });
    }

    /**
     * 从Redis中获取指定键对应的Set集合的所有元素。
     * <p>
     * 该方法通过RedisTemplate的execute方法执行Redis连接上的sMembers命令，获取指定键的Set集合。
     * 如果Set集合为空，则返回空集。否则，将Set集合中的每个字节数组元素转换为指定类型的对象，
     * 并返回转换后的对象集合。
     */
    public static <T> Set<T> sGetAll(String key, Class<T> clz) {
        // 使用RedisCallback包装Redis操作逻辑，以便通过RedisTemplate执行。
        return template.execute(new RedisCallback<Set<T>>() {
            @Override
            public Set<T> doInRedis(RedisConnection connection) throws DataAccessException {
                // 通过键获取Set集合中的所有字节数组元素。
                Set<byte[]> set = connection.sMembers(keyToBytes(key));
                // 如果集合为空，直接返回空集。
                if (CollectionUtils.isEmpty(set)) {
                    return Collections.emptySet();
                }
                // 将字节数组元素转换为指定类型的对象，并返回转换后的对象集合。
                return set.stream().map(s -> toObj(s, clz)).collect(Collectors.toSet());
            }
        });
    }


    /**
     * 移除set中的内容
     */
    public static <T> void sRem(String key, T val) {
        template.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.sRem(keyToBytes(key), valToBytes(val));
                return null;
            }
        });
    }


    /***********************************
     *      zset（有序集合）数据结构api
     ***********************************/

    /**
     * 分数更新
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public static Double zIncrBy(String key, String value, Integer score) {
        return template.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.zIncrBy(keyToBytes(key), score, valToBytes(value));
            }
        });
    }

    /**
     * 获取有序集合中成员的排名和分数信息。
     * <p>
     * 通过此方法可以同时获得有序集合中特定成员的排名和分数，方便后续处理。
     * 排名按照分数从低到高排列，排名越小表示分数越高。
     */
    public static ImmutablePair<Integer, Double> zRankInfo(String key, String value) {
        // 计算成员的分数
        double score = zScore(key, value);
        // 计算成员的排名
        int rank = zRank(key, value);
        // 将排名和分数封装成不可变对返回
        return ImmutablePair.of(rank, score);
    }

    /**
     * 获取分数
     *
     * @param key
     * @param value
     * @return
     */
    public static Double zScore(String key, String value) {
        return template.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.zScore(keyToBytes(key), valToBytes(value));
            }
        });
    }

    /**
     * 获取Redis中有序集合中成员的排名。
     * 该方法通过将key和value转换为字节数组，然后调用RedisConnection的zRank方法来实现。
     * 排名是基于成员在有序集合中的分数决定的，分数越小排名越靠前。
     */
    public static Integer zRank(String key, String value) {
        // 使用RedisTemplate的execute方法执行Redis回调操作。
        return template.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                // 将key和value转换为字节数组，然后调用zRank方法获取排名。
                // 注意：这里需要将字符串转换为字节数组，因为Redis的API使用字节数组作为参数。
                return connection.zRank(keyToBytes(key), valToBytes(value)).intValue();
            }
        });
    }


    /**
     * 从Redis的有序集合中获取排名前n的元素及其分数。
     * 该方法通过与Redis的交互，检索指定有序集合中分数最高的n个元素。有序集合中的元素按分数排序，
     * 这个方法返回的是一个包含元素及其对应分数的列表，分数从高到低排列。
     */
    public static List<ImmutablePair<String, Double>> zTopNScore(String key, int n) {
        // 使用RedisCallback来定义在Redis连接中执行的操作
        return template.execute(new RedisCallback<List<ImmutablePair<String, Double>>>() {
            @Override
            public List<ImmutablePair<String, Double>> doInRedis(RedisConnection connection) throws DataAccessException {
                // 从Redis中获取key对应的有序集合的最后n个元素及其分数
                Set<Tuple> set = connection.zRangeWithScores(keyToBytes(key), -n, -1);
                // 如果集合为空，则返回空列表
                if (set == null) {
                    return Collections.emptyList();
                }
                // 将获取到的元素及其分数转换为ImmutablePair，并按分数降序排序
                return set.stream()
                        .map(tuple -> ImmutablePair.of(toObj(tuple.getValue(), String.class), tuple.getScore()))
                        .sorted((o1, o2) -> Double.compare(o2.getRight(), o1.getRight())).collect(Collectors.toList());
            }
        });
    }


    /***********************************
     *      list（列表）数据结构api
     ***********************************/

    public static <T> Long lPush(String key, T val) {
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.lPush(keyToBytes(key), valToBytes(val));
            }
        });
    }

    public static <T> Long rPush(String key, T val) {
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.rPush(keyToBytes(key), valToBytes(val));
            }
        });
    }

    /**
     * 从Redis中获取指定key的列表的子列表，
     * 并将它们转换为指定类型的集合。
     */
    public static <T> List<T> lRange(String key, int start, int size, Class<T> clz) {
        // 使用Redis模板执行回调操作，以获取指定key的列表子集并进行类型转换
        return template.execute(new RedisCallback<List<T>>() {

            @Override
            public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
                // 从Redis中获取指定范围的列表数据
                List<byte[]> list = connection.lRange(keyToBytes(key), start, size);
                // 如果列表为空，则直接返回空列表
                if (CollectionUtils.isEmpty(list)) {
                    return new ArrayList<>();
                }
                // 将字节类型的列表元素转换为目标类型，并收集到一个列表中
                return list.stream().map(k -> toObj(k, clz))
                        .collect(Collectors.toList());
            }
        });
    }


    /**
     * 删除List中指定范围之外的元素，实现列表的左截断。
     * 仅保留列表中从start到start+size的元素，其余元素被移除。
     */
    public static void lTrim(String key, int start, int size) {
        // 使用RedisCallback封装Redis操作，确保操作在Redis连接中执行。
        template.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                // 将键名转换为字节数组，以适应Redis的字节操作。
                connection.lTrim(keyToBytes(key), start, size);
                return null; // RedisCallback的执行结果类型为Void，此处返回null。
            }
        });
    }


    public static PipelineAction pipelineAction() {
        return new PipelineAction();
    }

    /**
     * redis 管道执行的封装链路
     */
    public static class PipelineAction {
        private List<Runnable> run = new ArrayList<>();

        private RedisConnection connection;

        public PipelineAction add(String key, BiConsumer<RedisConnection, byte[]> conn) {
            run.add(() -> conn.accept(connection, RedisUtil.keyToBytes(key)));
            return this;
        }

        public PipelineAction add(String key, String field, ThreeConsumer<RedisConnection, byte[], byte[]> conn) {
            run.add(() -> conn.accept(connection, RedisUtil.keyToBytes(key), valToBytes(field)));
            return this;
        }

        public void execute() {
            template.executePipelined((RedisCallback<Object>) connection -> {
                PipelineAction.this.connection = connection;
                run.forEach(Runnable::run);
                return null;
            });
        }
    }

    @FunctionalInterface
    public interface ThreeConsumer<T, U, P> {
        void accept(T t, U u, P p);
    }
}
