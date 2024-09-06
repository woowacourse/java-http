package cache.com.example.cachecontrol;

import static com.google.common.net.HttpHeaders.CACHE_CONTROL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;

public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String cacheControl = CacheControl
                .noCache()
                .cachePrivate()
                .getHeaderValue();

        response.setHeader(CACHE_CONTROL, cacheControl);
        return true;
    }
}
