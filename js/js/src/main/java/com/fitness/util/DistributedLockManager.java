package com.fitness.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁管理器
 * 基于Redis实现分布式锁，使用Lua脚本保证原子性
 * 用于处理并发预约场景，防止超额预约和数据不一致
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockManager {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 锁的键前缀
     */
    private static final String LOCK_PREFIX = "lock:";

    /**
     * 当前线程持有的锁标识
     */
    private static final ThreadLocal<String> LOCK_IDENTIFIER = new ThreadLocal<>();

    /**
     * 获取锁的Lua脚本
     * 使用SET NX EX命令，保证原子性
     */
    private static final String LOCK_SCRIPT = 
        "if redis.call('exists', KEYS[1]) == 0 then " +
        "  redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2]) " +
        "  return 1 " +
        "else " +
        "  return 0 " +
        "end";

    /**
     * 释放锁的Lua脚本
     * 只有持有锁的线程才能释放，防止误删其他线程的锁
     */
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "  return redis.call('del', KEYS[1]) " +
        "else " +
        "  return 0 " +
        "end";

    /**
     * 尝试获取分布式锁
     *
     * @param key     锁的键（业务标识，如 "equipment:123"）
     * @param timeout 锁的超时时间
     * @param unit    时间单位
     * @return 是否成功获取锁
     */
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        String identifier = UUID.randomUUID().toString();
        long timeoutSeconds = unit.toSeconds(timeout);

        try {
            // 执行Lua脚本获取锁
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(LOCK_SCRIPT);
            script.setResultType(Long.class);

            Long result = redisTemplate.execute(
                script,
                Collections.singletonList(lockKey),
                identifier,
                String.valueOf(timeoutSeconds)
            );

            if (result != null && result == 1) {
                // 成功获取锁，保存标识符到ThreadLocal
                LOCK_IDENTIFIER.set(identifier);
                log.debug("Successfully acquired lock for key: {}, identifier: {}", key, identifier);
                return true;
            } else {
                log.debug("Failed to acquire lock for key: {}", key);
                return false;
            }
        } catch (Exception e) {
            log.error("Error while trying to acquire lock for key: {}", key, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param key 锁的键（业务标识）
     */
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        String identifier = LOCK_IDENTIFIER.get();

        if (identifier == null) {
            log.warn("No lock identifier found for key: {}, lock may not be held by current thread", key);
            return;
        }

        try {
            // 执行Lua脚本释放锁
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(UNLOCK_SCRIPT);
            script.setResultType(Long.class);

            Long result = redisTemplate.execute(
                script,
                Collections.singletonList(lockKey),
                identifier
            );

            if (result != null && result == 1) {
                log.debug("Successfully released lock for key: {}, identifier: {}", key, identifier);
            } else {
                log.warn("Failed to release lock for key: {}, lock may have expired or been released", key);
            }
        } catch (Exception e) {
            log.error("Error while trying to release lock for key: {}", key, e);
        } finally {
            // 清除ThreadLocal中的标识符
            LOCK_IDENTIFIER.remove();
        }
    }

    /**
     * 尝试获取锁（带重试）
     * 在指定的等待时间内重复尝试获取锁
     *
     * @param key         锁的键（业务标识）
     * @param lockTimeout 锁的超时时间
     * @param waitTimeout 等待获取锁的超时时间
     * @param unit        时间单位
     * @return 是否成功获取锁
     */
    public boolean tryLockWithRetry(String key, long lockTimeout, long waitTimeout, TimeUnit unit) {
        long waitTimeMillis = unit.toMillis(waitTimeout);
        long startTime = System.currentTimeMillis();
        long retryInterval = 50; // 重试间隔50毫秒

        while (System.currentTimeMillis() - startTime < waitTimeMillis) {
            if (tryLock(key, lockTimeout, unit)) {
                return true;
            }

            // 等待一段时间后重试
            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Thread interrupted while waiting for lock: {}", key);
                return false;
            }
        }

        log.debug("Failed to acquire lock for key: {} after waiting {} ms", key, waitTimeMillis);
        return false;
    }

    /**
     * 检查锁是否存在
     *
     * @param key 锁的键（业务标识）
     * @return 锁是否存在
     */
    public boolean isLocked(String key) {
        String lockKey = LOCK_PREFIX + key;
        Boolean exists = redisTemplate.hasKey(lockKey);
        return exists != null && exists;
    }

    /**
     * 强制释放锁（管理员操作，慎用）
     * 不检查锁的持有者，直接删除锁
     *
     * @param key 锁的键（业务标识）
     */
    public void forceUnlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.delete(lockKey);
        log.warn("Force unlocked key: {}", key);
    }
}
