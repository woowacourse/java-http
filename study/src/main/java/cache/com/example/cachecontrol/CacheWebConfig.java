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
        registry.addInterceptor(createNoCacheControlWebContentInterceptor());
        registry.addInterceptor(createResourceWebContentInterceptor());
    }

    private WebContentInterceptor createNoCacheControlWebContentInterceptor() {
        CacheControl cacheControl = CacheControl.noCache()
                .cachePrivate();
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(cacheControl, "/");
        return webContentInterceptor;
    }

    private WebContentInterceptor createResourceWebContentInterceptor() {
        CacheControl cacheControl1 = CacheControl.maxAge(Duration.ofDays(365))
                .cachePublic();
        WebContentInterceptor webContentInterceptor1 = new WebContentInterceptor();
        webContentInterceptor1.addCacheMapping(cacheControl1, "/resources/**");
        return webContentInterceptor1;
    }
}
