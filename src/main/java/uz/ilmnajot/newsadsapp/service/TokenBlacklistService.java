package uz.ilmnajot.newsadsapp.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import uz.ilmnajot.newsadsapp.security.JwtProvider;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    /**
     * Add token to blacklist
     * 
     * @param token JWT token to blacklist
     * @param reason Reason for blacklisting (LOGOUT, SECURITY, etc.)
     */
    public void blacklistToken(String token, String reason) {
        try {
            // Extract token ID (jti) or use token hash
            String tokenId = getTokenIdentifier(token);
            
            // Get expiration time from token
            Date expiration = jwtProvider.getExpirationDateFromToken(token);
            
            // Calculate TTL (time until token expires)
            long ttlSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            
            // Only blacklist if token hasn't expired yet
            if (ttlSeconds > 0) {
                String key = BLACKLIST_PREFIX + tokenId;
                
                // Store metadata
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("reason", reason);
                metadata.put("blacklistedAt", System.currentTimeMillis());
                metadata.put("expiresAt", expiration.getTime());
                
                // Add username if available
                try {
                    String username = jwtProvider.getUsernameFromToken(token);
                    metadata.put("username", username);
                } catch (Exception e) {
                    log.warn("Could not extract username from token");
                }
                
                // Store in Redis with TTL
                redisTemplate.opsForValue().set(key, metadata, ttlSeconds, TimeUnit.SECONDS);
                
                log.info("Token blacklisted: {} (reason: {}, TTL: {}s)", 
                         tokenId.substring(0, 10) + "...", reason, ttlSeconds);
            } else {
                log.debug("Token already expired, not adding to blacklist");
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage(), e);
            // Don't throw exception - allow logout to proceed
        }
    }

    /**
     * Check if token is blacklisted
     * 
     * @param token JWT token to check
     * @return true if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String tokenId = getTokenIdentifier(token);
            String key = BLACKLIST_PREFIX + tokenId;
            
            Boolean hasKey = redisTemplate.hasKey(key);
            
            if (Boolean.TRUE.equals(hasKey)) {
                log.warn("Blacklisted token used: {}", tokenId.substring(0, 10) + "...");
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage());
            // Fail-safe: if Redis is down, don't block all requests
            return false;
        }
    }

    /**
     * Remove token from blacklist (for testing or manual intervention)
     * 
     * @param token JWT token to remove
     */
    public void removeFromBlacklist(String token) {
        try {
            String tokenId = getTokenIdentifier(token);
            String key = BLACKLIST_PREFIX + tokenId;
            redisTemplate.delete(key);
            
            log.info("Token removed from blacklist: {}", tokenId.substring(0, 10) + "...");
        } catch (Exception e) {
            log.error("Error removing token from blacklist: {}", e.getMessage());
        }
    }

    /**
     * Get all blacklisted tokens (for admin panel)
     * 
     * WARNING: This can be slow with many tokens!
     */
    public Map<String, Object> getBlacklistStats() {
        try {
            Set<String> keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBlacklisted", keys != null ? keys.size() : 0);
            stats.put("timestamp", System.currentTimeMillis());
            
            return stats;
        } catch (Exception e) {
            log.error("Error getting blacklist stats: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * Blacklist all tokens for a specific user (on password change, account lock, etc.)
     * 
     * Note: This requires tracking user tokens separately
     */
    public void blacklistAllUserTokens(String username, String reason) {
        // This would require maintaining a separate index of user -> tokens
        // For now, we blacklist tokens individually on logout
        log.info("Blacklist all tokens for user: {} (reason: {})", username, reason);
        
        // TODO: Implement user token tracking if needed
    }

    /**
     * Get unique identifier for token
     * Options:
     * 1. Use 'jti' claim if present (recommended)
     * 2. Hash the full token
     * 3. Use signature part of JWT
     */
    private String getTokenIdentifier(String token) {
        try {
            // Try to extract 'jti' (JWT ID) claim
            Claims claims = jwtProvider.getAllClaimsFromToken(token);
            String jti = claims.getId();
            
            if (jti != null && !jti.isEmpty()) {
                return jti;
            }
        } catch (Exception e) {
            // If jti not available, fall back to token hash
        }
        
        // Fall back to hashing the signature part
        String[] parts = token.split("\\.");
        if (parts.length == 3) {
            // Use signature (last part) as identifier
            return parts[2];
        }
        
        // Last resort: hash full token (not recommended for performance)
        return String.valueOf(token.hashCode());
    }

    /**
     * Clean up expired entries (Redis TTL handles this automatically)
     * This method is for manual cleanup if needed
     */
    public void cleanupExpiredTokens() {
        log.info("Redis TTL automatically handles expired tokens cleanup");
        // Redis automatically removes keys when TTL expires
        // No manual cleanup needed
    }
}