package com.observx.collector.filter;

import com.observx.collector.config.RateLimitingConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Servlet filter for rate limiting incoming requests.
 * Returns HTTP 429 (Too Many Requests) when limit exceeded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter implements Filter {

    private final RateLimitingConfig rateLimitingConfig;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get client identifier (IP address or API key from header)
        String clientId = getClientId(httpRequest);
        Bucket bucket = rateLimitingConfig.resolveBucket(clientId);

        // Try to consume 1 token
        if (bucket.tryConsume(1)) {
            // Request allowed - continue processing
            chain.doFilter(request, response);
        } else {
            // Rate limit exceeded - return 429
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Please try again later.\"}"
            );
            log.warn("Rate limit exceeded for client: {}", clientId);
        }
    }

    /**
     * Get client identifier for rate limiting
     * Priority: API Key header > IP Address
     */
    private String getClientId(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return "api:" + apiKey;
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return "ip:" + ipAddress;
    }
}
