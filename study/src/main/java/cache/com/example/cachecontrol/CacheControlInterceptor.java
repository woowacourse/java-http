package cache.com.example.cachecontrol;

import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CacheControl cacheControl = CacheControl.noCache().cachePrivate();
        String cacheControlValue = cacheControl.getHeaderValue();
        response.addHeader(CACHE_CONTROL, cacheControlValue);
        return true;
    }
}
