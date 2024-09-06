package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class NoCachePrivateInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        response.addHeader("Cache-Control", "no-cache, private");
//        response.addHeader("Content-Encoding", "gzip");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
