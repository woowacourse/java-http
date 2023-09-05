package cache.com.example.cachecontrol;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CacheInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        response.addHeader(CACHE_CONTROL, "no-cache");
        response.addHeader(CACHE_CONTROL, "private");
        return true;
    }
}
