package cache.com.example.cachecontrol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class NoCacheInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final ModelAndView modelAndView
    ) throws Exception {

        response.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache, private");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
