package cache.com.example.cachecontrol;

import com.google.common.net.HttpHeaders;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception {
        final String cacheControlValue = CacheControl.noCache()
                .cachePrivate()
                .getHeaderValue();

        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControlValue);
    }
}
