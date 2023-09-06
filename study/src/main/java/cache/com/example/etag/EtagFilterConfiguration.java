package cache.com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    private static final String ETAG_PATH = "/etag";
    private static final String RESOURCES_PATH = "/resources/*";

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filters = new FilterRegistrationBean<>();

        filters.setFilter(new ShallowEtagHeaderFilter());
        filters.addUrlPatterns(ETAG_PATH);
        filters.addUrlPatterns(RESOURCES_PATH);

        return filters;
    }
}
