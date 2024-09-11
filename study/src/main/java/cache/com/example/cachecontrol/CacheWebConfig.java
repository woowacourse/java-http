package cache.com.example.cachecontrol;

import static java.util.concurrent.TimeUnit.DAYS;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        WebContentInterceptor noCacheIntercepter = new WebContentInterceptor();
        noCacheIntercepter.addCacheMapping(CacheControl.noCache().cachePrivate(), "/**");
        registry.addInterceptor(noCacheIntercepter)
                .excludePathPatterns("/**/*.js", "/**/*.css");

        WebContentInterceptor cacheIntercepter = new WebContentInterceptor();
        cacheIntercepter.addCacheMapping(CacheControl.maxAge(365, DAYS).cachePublic(), "/**");
        registry.addInterceptor(cacheIntercepter)
                .addPathPatterns("/**/*.js", "/**/*.css");
    }
}
