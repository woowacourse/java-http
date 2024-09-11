package cache.com.example.cachecontrol;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        WebContentInterceptor cacheInterceptor = new WebContentInterceptor();
        cacheInterceptor.setCacheControl(CacheControl.noCache().cachePrivate());
        registry.addInterceptor(cacheInterceptor)
                .excludePathPatterns(PREFIX_STATIC_RESOURCES + "/**");

        WebContentInterceptor resourceInterceptor = new WebContentInterceptor();
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365)).cachePublic();
        resourceInterceptor.setCacheControl(cacheControl);
        registry.addInterceptor(resourceInterceptor)
                .addPathPatterns(PREFIX_STATIC_RESOURCES + "/**");
    }
}
