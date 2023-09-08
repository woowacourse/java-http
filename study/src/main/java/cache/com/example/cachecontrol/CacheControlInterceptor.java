package cache.com.example.cachecontrol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
        final ModelAndView modelAndView) {
        if (response.getHeader("Cache-Control") == null) {
            response.addHeader("Cache-Control", "no-cache, private");
        }
    }
}
