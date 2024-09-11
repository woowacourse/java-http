package cache.com.example.etag;

import cache.com.example.version.CacheBustingWebConfig;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new ShallowEtagHeaderFilter());
        filter.addUrlPatterns("/etag", CacheBustingWebConfig.PREFIX_STATIC_RESOURCES + "/*");
        return filter;
    }
}
