package cache.com.example.cachecontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {


    private static final Logger log = LoggerFactory.getLogger(CacheWebConfig.class);

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final CacheControl cacheControl = CacheControl.noCache().cachePrivate();
        log.info("Add interceptor : {}", cacheControl.getHeaderValue());

        final WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(cacheControl, "/**");
        registry.addInterceptor(webContentInterceptor);
    }
}
