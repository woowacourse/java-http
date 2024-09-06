package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

public class OneYearCacheInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String cacheControl = CacheControl
                .maxAge(Duration.ofDays(365))
                .cachePublic()
                .getHeaderValue();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
        return true;
    }
}
