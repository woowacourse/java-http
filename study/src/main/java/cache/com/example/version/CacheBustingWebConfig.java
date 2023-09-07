package cache.com.example.version;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheBustingWebConfig implements WebMvcConfigurer {

    public static final String PREFIX_STATIC_RESOURCES = "/resources";

    private final ResourceVersion version;

    @Autowired
    public CacheBustingWebConfig(ResourceVersion version) {
        this.version = version;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler(PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/**")
                .addResourceLocations("classpath:/static/")
                // ETag를 사용하니까 Last-Modified는 불필요하지 않을까?
                // Last-Modified를 추가하면 캐싱 외에도 크롤러에게 마지막 수정 시간을 알려주어 크롤링 빈도를 조정할 수 있다.
                // 자세한 내용은 아래 링크를 참고하자.
                // https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching#sect1
                .setUseLastModified(true)
                .setCacheControl(CacheControl.maxAge(365L, TimeUnit.DAYS).cachePublic());
    }
}
