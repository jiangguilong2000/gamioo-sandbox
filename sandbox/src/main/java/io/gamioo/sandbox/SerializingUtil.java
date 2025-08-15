package io.gamioo.sandbox;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化和反序列化工具类
 * @author Allen Jiang
 */
public class SerializingUtil {

    // 使用缓存避免重复创建RuntimeSchema
    private static final ConcurrentHashMap<Class<?>, Schema<?>> SCHEMA_CACHE = new ConcurrentHashMap<>();

    // 使用ThreadLocal避免频繁创建LinkedBuffer
    private static final ThreadLocal<LinkedBuffer> BUFFER_THREAD_LOCAL = ThreadLocal.withInitial(
            () -> LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    /**
     * 将目标类序列化为byte数组
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T source) {
        if (source == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) source.getClass();
        Schema<T> schema = getSchema(clazz);
        LinkedBuffer buffer = BUFFER_THREAD_LOCAL.get();

        try {
            return ProtostuffIOUtil.toByteArray(source, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 将byte数组反序列化为目标类
     *
     * @param source
     * @param typeClass
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] source, Class<T> typeClass) {
        if (source == null || typeClass == null) {
            return null;
        }

        Schema<T> schema = getSchema(typeClass);
        T newInstance;
        try {
            newInstance = typeClass.getDeclaredConstructor().newInstance();
            ProtostuffIOUtil.mergeFrom(source, newInstance, schema);
        } catch (Exception e) {
            throw new RuntimeException("deserialize exception", e);
        }
        return newInstance;
    }

    /**
     * 获取类的RuntimeSchema，如果不存在则创建并缓存
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        return (Schema<T>) SCHEMA_CACHE.computeIfAbsent(clazz, RuntimeSchema::createFrom);
    }
}
