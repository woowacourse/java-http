package cache.com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> registrationBean = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        registrationBean.addUrlPatterns("/etag");
        registrationBean.addUrlPatterns("/resources/*");
        return registrationBean;
    }
}
