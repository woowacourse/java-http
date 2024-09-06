package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CacheHandlerInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        response.addHeader("Cache-Control", "no-cache, private");
    }
}
