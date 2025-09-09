package cache.com.example.cachecontrol;

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
        final WebContentInterceptor interceptor = new WebContentInterceptor();
        interceptor.addCacheMapping(
                CacheControl
                        .maxAge(Duration.ofDays(365))
                        .cachePublic(),
                "/resources/**"
        );
        registry.addInterceptor(interceptor);

        final WebContentInterceptor interceptor2 = new WebContentInterceptor();
        interceptor2.addCacheMapping(
                CacheControl
                        .maxAge(Duration.ofDays(365))
                        .cachePublic(),
                "/resources/**"
        );
        registry.addInterceptor(interceptor2);
    }
}
