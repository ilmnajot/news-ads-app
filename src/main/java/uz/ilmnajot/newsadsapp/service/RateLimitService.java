package uz.ilmnajot.newsadsapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Check if request is allowed (Simple counter method)
     * 
     * @param key Unique key (IP:endpoint)
     * @param limit Max requests
     * @param duration Duration in seconds
     * @return true if allowed, false if rate limited
     */
    public boolean isAllowedSimple(String key, int limit, long duration) {
        
        String redisKey = "rate_limit:" + key;
        
        // Get current count
        Long count = redisTemplate.opsForValue().increment(redisKey);
        
        if (count == null) {
            count = 0L;
        }
        
        // Set expiry on first request
        if (count == 1) {
            redisTemplate.expire(redisKey, duration, TimeUnit.SECONDS);
        }
        
        boolean allowed = count <= limit;
        
        if (!allowed) {
            log.warn("Rate limit exceeded: key={}, count={}/{}", key, count, limit);
        }
        
        return allowed;
    }

    /**
     * Check if request is allowed (Sliding Window method - BETTER!)
     * 
     * Uses Redis sorted set with timestamps
     */
    public boolean isAllowed(String key, int limit, long durationSeconds) {
        
        String redisKey = "rate_limit:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - (durationSeconds * 1000);
        
        // Remove old entries (outside window)
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        
        // Get current count
        Long count = redisTemplate.opsForZSet().size(redisKey);
        
        if (count == null) {
            count = 0L;
        }
        
        if (count < limit) {
            // Add current request
            redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);
            
            // Set expiry
            redisTemplate.expire(redisKey, durationSeconds + 10, TimeUnit.SECONDS);
            
            log.debug("Request allowed: key={}, count={}/{}", key, count + 1, limit);
            return true;
        }
        
        log.warn("Rate limit exceeded: key={}, count={}/{}", key, count, limit);
        return false;
    }

    /**
     * Check with Lua script (Atomic operation - BEST!)
     */
    public boolean isAllowedAtomic(String key, int limit, long durationSeconds) {
        
        String redisKey = "rate_limit:" + key;
        
        // Lua script for atomic increment + expiry
        String luaScript = 
            "local current = redis.call('incr', KEYS[1]) " +
            "if current == 1 then " +
            "    redis.call('expire', KEYS[1], ARGV[1]) " +
            "end " +
            "return current";
        
        RedisScript<Long> script = RedisScript.of(luaScript, Long.class);
        
        Long count = redisTemplate.execute(
            script,
            Collections.singletonList(redisKey),
            String.valueOf(durationSeconds)
        );
        
        if (count == null) {
            count = 0L;
        }
        
        boolean allowed = count <= limit;
        
        if (!allowed) {
            log.warn("Rate limit exceeded (atomic): key={}, count={}/{}", key, count, limit);
        }
        
        return allowed;
    }

    /**
     * Get remaining requests
     */
    public long getRemainingRequests(String key, int limit, long durationSeconds) {
        
        String redisKey = "rate_limit:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - (durationSeconds * 1000);
        
        // Remove old entries
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        
        // Get current count
        Long count = redisTemplate.opsForZSet().size(redisKey);
        
        if (count == null) {
            return limit;
        }
        
        return Math.max(0, limit - count);
    }

    /**
     * Get time until reset (in seconds)
     */
    public long getTimeUntilReset(String key) {
        
        String redisKey = "rate_limit:" + key;
        
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        
        return ttl != null ? ttl : 0;
    }

    /**
     * Reset rate limit for a key (for testing or admin actions)
     */
    public void reset(String key) {
        
        String redisKey = "rate_limit:" + key;
        redisTemplate.delete(redisKey);
        
        log.info("Rate limit reset: key={}", key);
    }
}