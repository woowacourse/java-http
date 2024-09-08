package cache.com.example.cachecontrol;

import com.google.common.net.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;

public class CacheHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CacheControl cacheControl = CacheControl.noCache().cachePrivate();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.getHeaderValue());

        return true;
    }
}
