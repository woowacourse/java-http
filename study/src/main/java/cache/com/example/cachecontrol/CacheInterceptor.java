package cache.com.example.cachecontrol;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        response.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache, private");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
