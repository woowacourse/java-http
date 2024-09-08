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

    private final ResourceVersion resourceVersion;

    @Autowired
    public CacheWebConfig(final ResourceVersion resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        WebContentInterceptor noCachePrivateInterceptor = new WebContentInterceptor();
        noCachePrivateInterceptor.setCacheControl(CacheControl.noCache().cachePrivate());
        registry.addInterceptor(noCachePrivateInterceptor)
                .addPathPatterns("/");

        WebContentInterceptor burstCacheInterceptor = new WebContentInterceptor();
        burstCacheInterceptor.setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic());
        registry.addInterceptor(burstCacheInterceptor)
                .addPathPatterns("/resources/" + resourceVersion.getVersion() + "/**");
    }
}
