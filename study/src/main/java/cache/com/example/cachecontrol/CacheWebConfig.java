package cache.com.example.cachecontrol;

import com.google.common.net.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new CacheHandlerInterceptor()).addPathPatterns("/");
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365)).cachePublic();
                response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.getHeaderValue());
                return true;
            }
        }).addPathPatterns("/resources/**");
    }
}
