package cache.com.example.cachecontrol;

import cache.com.example.version.ResourceVersion;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final ResourceVersion version;
    private final CacheControlInterceptor cacheControlInterceptor;

    public CacheWebConfig(ResourceVersion version, CacheControlInterceptor cacheControlInterceptor) {
        this.version = version;
        this.cacheControlInterceptor = cacheControlInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(cacheControlInterceptor)
                .addPathPatterns("/");

    }
}
