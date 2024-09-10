package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.common.net.HttpHeaders;

public class CacheInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {
        CacheControl cacheControl = CacheControl.noCache().cachePrivate();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.getHeaderValue());
        return true;
    }
}
