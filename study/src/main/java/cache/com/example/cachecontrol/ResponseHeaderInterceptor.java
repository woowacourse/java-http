package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ResponseHeaderInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        response.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache, private");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
