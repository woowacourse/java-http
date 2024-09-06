package cache.com.example.etag;

import cache.com.example.version.ResourceVersion;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

@Configuration
public class EtagFilterConfiguration {

    private final ResourceVersion resourceVersion;

    public EtagFilterConfiguration(ResourceVersion resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterFilterRegistrationBean
                = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filterFilterRegistrationBean.addUrlPatterns("/etag");
        filterFilterRegistrationBean.addUrlPatterns(PREFIX_STATIC_RESOURCES + "/" + resourceVersion.getVersion() + "/*");
        return filterFilterRegistrationBean;
    }
}
