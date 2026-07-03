package com.learn.tasks.config;

// [STEP 1 & 9] Servlet Filter — the FIRST and LAST code to touch every request (≈ NestJS middleware).
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component // @Component → Spring auto-registers it into the servlet filter chain (no manual wiring)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String correlationId = UUID.randomUUID().toString().substring(0, 8); // one id to trace this request across all logs
        MDC.put("correlationId", correlationId);                              // put it in MDC so the log pattern prints it
        long start = System.currentTimeMillis();
        log.info("[STEP 1] >>> incoming {} {}", req.getMethod(), req.getRequestURI()); // entering the app
        try {
            chain.doFilter(req, res); // [STEP 2] hand off to DispatcherServlet -> interceptor -> controller
        } finally {
            long ms = System.currentTimeMillis() - start;
            log.info("[STEP 9] <<< completed {} {} -> {} in {}ms", req.getMethod(), req.getRequestURI(), res.getStatus(), ms); // leaving the app
            MDC.clear(); // clear so the id doesn't leak to the next request reusing this thread
        }
    }
}
