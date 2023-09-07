package cache.com.example.cachecontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(cacheInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public HandlerInterceptor cacheInterceptor() {
        final WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.setCacheControl(CacheControl.noCache().cachePrivate());

        return webContentInterceptor;
    }
}
