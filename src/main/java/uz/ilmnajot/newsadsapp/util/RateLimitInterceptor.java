package uz.ilmnajot.newsadsapp.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.handler.advice.RateLimiterRequestHandlerAdvice;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uz.ilmnajot.newsadsapp.annotation.RateLimit;
import uz.ilmnajot.newsadsapp.exception.RateLimitExceededException;
import uz.ilmnajot.newsadsapp.service.RateLimitService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        
        if (rateLimit == null) {
            return true;  // No rate limit
        }
        
        // Get client identifier
        String clientIp = getClientIp(request);
        String endpoint = request.getRequestURI();
        String key = clientIp + ":" + endpoint;
        
        // Convert duration to seconds
        long durationSeconds = TimeUnit.SECONDS.convert(
            rateLimit.duration(), 
            rateLimit.timeUnit()
        );
        
        // Check rate limit (using Redis)
        boolean allowed = rateLimitService.isAllowed(
            key,
            rateLimit.limit(),
            durationSeconds
        );
        
        if (!allowed) {
            // Get remaining and reset time
            long remaining = rateLimitService.getRemainingRequests(
                key, 
                rateLimit.limit(), 
                durationSeconds
            );
            
            long resetTime = rateLimitService.getTimeUntilReset(key);
            
            log.warn("Rate limit exceeded: ip={}, endpoint={}, limit={}/{} seconds", 
                clientIp, endpoint, rateLimit.limit(), durationSeconds);

            // Set headers
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.limit()));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(resetTime));
            response.setHeader("Retry-After", String.valueOf(resetTime));

            throw new RateLimitExceededException(
                rateLimit.message() + " Try again in " + resetTime + " seconds."
            );
        }
        
        // Set rate limit headers
        long remaining = rateLimitService.getRemainingRequests(
            key,
            rateLimit.limit(),
            durationSeconds
        );
        
        response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        
        return true;
    }

    /**
     * Get client IP address (handles proxies)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // If multiple IPs (proxy chain), take first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}