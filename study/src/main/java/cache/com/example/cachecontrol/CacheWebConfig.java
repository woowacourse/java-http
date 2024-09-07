package cache.com.example.cachecontrol;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final CacheControlInterceptor cacheControlInterceptor;

    public CacheWebConfig(CacheControlInterceptor cacheControlInterceptor) {
        this.cacheControlInterceptor = cacheControlInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(cacheControlInterceptor)
                .addPathPatterns("/");
    }
}
