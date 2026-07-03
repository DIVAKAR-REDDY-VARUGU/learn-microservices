package com.learn.tasks.config;

// [STEP 3,7,8] HandlerInterceptor — runs around the controller AFTER routing (≈ NestJS interceptor).
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override // [STEP 3] runs BEFORE the controller method — return false here to block the request (a "guard" lives here)
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        log.info("[STEP 3] preHandle -> matched handler = {}", handler);
        return true; // true = continue to the controller; false = stop here
    }

    @Override // [STEP 7] runs AFTER the controller returned, before the response body is written
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView mv) {
        log.info("[STEP 7] postHandle -> status so far = {}", res.getStatus());
    }

    @Override // [STEP 8] runs AFTER the full response is sent — always, even if an exception was thrown
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        log.info("[STEP 8] afterCompletion -> error = {}", ex == null ? "none" : ex.getMessage());
    }
}
