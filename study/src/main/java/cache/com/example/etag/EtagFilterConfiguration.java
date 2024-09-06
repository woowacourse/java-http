package cache.com.example.etag;

import cache.com.example.version.ResourceVersion;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

@Configuration
public class EtagFilterConfiguration {

    private final ResourceVersion version;

    public EtagFilterConfiguration(ResourceVersion resourceVersion) {
        this.version = resourceVersion;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean
                = new FilterRegistrationBean<>( new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/etag/*", PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/*");
        filterRegistrationBean.setName("etagFilter");
        return filterRegistrationBean;
    }
}
