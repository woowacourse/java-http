package cache.com.example.cachecontrol;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        CacheControl cacheControl = CacheControl.noCache().cachePrivate();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.getHeaderValue());
    }
}
