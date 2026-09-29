package cache.com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        final ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
        final FilterRegistrationBean<ShallowEtagHeaderFilter> registration =
            new FilterRegistrationBean<>(filter);

        registration.addUrlPatterns("/*");
        return registration;
    }
}
