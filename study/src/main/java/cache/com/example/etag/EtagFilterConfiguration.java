package cache.com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> registrationEtagFilter() {
        final FilterRegistrationBean<ShallowEtagHeaderFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new ShallowEtagHeaderFilter());
        filterFilterRegistrationBean.addUrlPatterns("/etag");
        filterFilterRegistrationBean.addUrlPatterns("/resources/*");
        return filterFilterRegistrationBean;
    }
}
