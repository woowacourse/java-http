package cache.com.example.cachecontrol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new CacheInterceptor());
    }

    class CacheInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                                 final Object handler)
                throws Exception {

            response.addHeader("Cache-Control", "no-cache");
            response.addHeader("Cache-Control", "private");

            return true;
        }
    }
}
