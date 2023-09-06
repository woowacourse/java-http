package cache.com.example.cachecontrol;

import cache.com.example.version.CacheBustingWebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final CacheInterceptor cacheInterceptor;

    public CacheWebConfig(final CacheInterceptor cacheInterceptor) {
        this.cacheInterceptor = cacheInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(cacheInterceptor)
                .excludePathPatterns(CacheBustingWebConfig.PREFIX_STATIC_RESOURCES + "/**");
    }
}
