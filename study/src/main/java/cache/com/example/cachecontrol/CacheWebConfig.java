package cache.com.example.cachecontrol;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(webContentInterceptor());
    }

    @Bean
    public WebContentInterceptor webContentInterceptor() {
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(CacheControl.noCache().cachePrivate(), "/**");
        return webContentInterceptor;
    }
}
