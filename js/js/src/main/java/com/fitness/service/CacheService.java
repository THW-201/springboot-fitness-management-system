package com.fitness.service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 * 提供Redis缓存操作的统一接口
 */
public interface CacheService {

    /**
     * 获取缓存值
     *
     * @param key  缓存键
     * @param type 值类型
     * @param <T>  泛型类型
     * @return 缓存值，不存在返回null
     */
    <T> T get(String key, Class<T> type);

    /**
     * 设置缓存值
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);

    /**
     * 设置缓存值（带过期时间）
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void delete(String key);

    /**
     * 批量删除缓存（支持通配符）
     *
     * @param pattern 键模式（如 "user:*"）
     */
    void deletePattern(String pattern);

    /**
     * 检查缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 设置缓存过期时间
     *
     * @param key     缓存键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 获取缓存剩余过期时间
     *
     * @param key  缓存键
     * @param unit 时间单位
     * @return 剩余时间，-1表示永不过期，-2表示不存在
     */
    long getExpire(String key, TimeUnit unit);
}
