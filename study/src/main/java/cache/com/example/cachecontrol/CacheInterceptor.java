package cache.com.example.cachecontrol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CacheInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        String cacheControl = CacheControl
                .noCache()
                .cachePrivate()
                .getHeaderValue();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl);

        return super.preHandle(request, response, handler);
    }
}
