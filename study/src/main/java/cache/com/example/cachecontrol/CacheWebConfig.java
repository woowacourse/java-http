package cache.com.example.cachecontrol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final NoCachePrivateInterceptor noCachePrivateInterceptor;
    private final CompressionInterceptor compressionInterceptor;
    public CacheWebConfig(NoCachePrivateInterceptor noCachePrivateInterceptor,
                          CompressionInterceptor compressionInterceptor) {
        this.noCachePrivateInterceptor = noCachePrivateInterceptor;
        this.compressionInterceptor = compressionInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(noCachePrivateInterceptor)
                .addPathPatterns("/");

//        registry.addInterceptor(compressionInterceptor)
//                .addPathPatterns("/");
    }
}
