package com.retention.intelligence.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        
        Bucket bucket = resolveBucket(ip, path);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }

    private Bucket resolveBucket(String ip, String path) {
        String key = ip;
        long capacity = 100;
        long tokens = 100;
        Duration period = Duration.ofMinutes(1);
        
        if (path.startsWith("/auth/")) {
            key = "auth_" + ip;
            capacity = 10;
            tokens = 10;
        }

        final long finalCapacity = capacity;
        final long finalTokens = tokens;
        
        return buckets.computeIfAbsent(key, k -> {
            Refill refill = Refill.greedy(finalTokens, period);
            Bandwidth limit = Bandwidth.classic(finalCapacity, refill);
            return Bucket.builder().addLimit(limit).build();
        });
    }
}
