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

        return redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
    }
}