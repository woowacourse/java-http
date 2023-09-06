package cache.com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        ShallowEtagHeaderFilter shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
        FilterRegistrationBean<ShallowEtagHeaderFilter> bean = new FilterRegistrationBean<>(shallowEtagHeaderFilter);
        bean.addUrlPatterns("/etag");
        bean.addUrlPatterns("/resources/*");
        return bean;
    }
}
