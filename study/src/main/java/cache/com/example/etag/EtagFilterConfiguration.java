package cache.com.example.etag;

import cache.com.example.version.ResourceVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    private final ResourceVersion resourceVersion;

    @Autowired
    public EtagFilterConfiguration(final ResourceVersion resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        ShallowEtagHeaderFilter shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean =
                new FilterRegistrationBean<>(shallowEtagHeaderFilter);
        filterRegistrationBean.addUrlPatterns("/etag", "/resources/" + resourceVersion.getVersion() + "/*");
        return filterRegistrationBean;
    }
}
