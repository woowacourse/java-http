package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class CacheInterceptorHandler implements HandlerInterceptor {

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView
    ) {
        String requestURI = request.getRequestURI();

        if (!requestURI.startsWith("/resources/") && !requestURI.startsWith("/static/")) {
            final String cacheControl = CacheControl.noCache().cachePrivate().getHeaderValue();
            response.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
        }
    }
}
