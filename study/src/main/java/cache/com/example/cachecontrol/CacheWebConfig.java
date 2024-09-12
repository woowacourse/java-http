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
        registry.addInterceptor(getNoCacheInterceptor());
        registry.addInterceptor(getResourceCacheInterceptor());
    }

    // add no-cache, private cache
    private WebContentInterceptor getNoCacheInterceptor() {
        CacheControl cacheControl = CacheControl.noCache().cachePrivate();

        return createWebContentInterceptorByCache(cacheControl, "/");
    }

    private WebContentInterceptor getResourceCacheInterceptor() {
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365)).cachePublic();

        return createWebContentInterceptorByCache(cacheControl, "/resources/**");
    }

    private WebContentInterceptor createWebContentInterceptorByCache(CacheControl cacheControl, String path) {
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(cacheControl, path);
        return webContentInterceptor;
    }
}
