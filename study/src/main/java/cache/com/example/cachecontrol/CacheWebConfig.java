package cache.com.example.cachecontrol;

import cache.com.example.version.ResourceVersion;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final CacheWebInterceptor cacheWebInterceptor;

    public CacheWebConfig(final CacheWebInterceptor cacheWebInterceptor, final ResourceVersion version) {
        this.cacheWebInterceptor = cacheWebInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(cacheWebInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/resources/static/**");
    }
}
