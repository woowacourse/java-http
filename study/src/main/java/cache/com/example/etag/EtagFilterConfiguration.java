package cache.com.example.etag;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> registration
                = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());

        registration.addUrlPatterns("/etag", PREFIX_STATIC_RESOURCES + "/*");
        registration.setName("EtagFilter");

        return registration;
    }
}
