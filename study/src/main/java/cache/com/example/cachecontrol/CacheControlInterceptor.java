package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        response.setHeader("Cache-Control", "no-cache, private");
        if (uri.endsWith(".js") || uri.endsWith(".css")) {
            response.setHeader("Cache-Control", "max-age=31536000, public");
        }
        return true;
    }
}
