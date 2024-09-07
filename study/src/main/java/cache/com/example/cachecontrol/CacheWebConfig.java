package cache.com.example.cachecontrol;

import cache.com.example.version.ResourceVersion;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final ResourceVersion version;

    @Autowired
    public CacheWebConfig(ResourceVersion version) {
        this.version = version;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        WebContentInterceptor noCacheInterceptor = new WebContentInterceptor();
        noCacheInterceptor.addCacheMapping(CacheControl.noCache().cachePrivate(), "/**");
        registry.addInterceptor(noCacheInterceptor);

        WebContentInterceptor cacheInterceptor = new WebContentInterceptor();
        cacheInterceptor.addCacheMapping(CacheControl.maxAge(Duration.ofDays(365L)), "/etag");
        cacheInterceptor.addCacheMapping(CacheControl.maxAge(Duration.ofDays(365L)).cachePublic(),
                "/resources/" + version.getVersion() + "/**");
        registry.addInterceptor(cacheInterceptor);
    }
}
