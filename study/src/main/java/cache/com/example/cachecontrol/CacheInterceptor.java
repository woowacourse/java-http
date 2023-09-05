package cache.com.example.cachecontrol;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;

public class CacheInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
        if (Objects.isNull(response.getHeader("Cache-Control"))) {
            response.addHeader("Cache-Control",
                    CacheControl.noCache().cachePrivate().getHeaderValue());
        }
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
