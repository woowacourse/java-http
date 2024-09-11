package cache.com.example.cachecontrol;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) throws Exception {
        if (!request.getRequestURI().startsWith(PREFIX_STATIC_RESOURCES)) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, private");
        }
    }
}
