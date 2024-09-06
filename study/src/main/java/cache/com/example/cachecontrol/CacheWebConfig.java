package cache.com.example.cachecontrol;

import cache.com.example.version.ResourceVersion;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final ResourceVersion version;

    public CacheWebConfig(ResourceVersion version) {
        this.version = version;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(
                CacheControl.noCache().cachePrivate(),
                "/**"
        );
        registry.addInterceptor(webContentInterceptor);
    }
}
