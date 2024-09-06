package cache.com.example.etag;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import cache.com.example.version.ResourceVersion;

@Configuration
public class EtagFilterConfiguration {

    private final ResourceVersion version;

    @Autowired
    public EtagFilterConfiguration(ResourceVersion version) {
        this.version = version;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/etag");
        filterRegistrationBean.addUrlPatterns("/resources/" + version.getVersion() + "/js/index.js");

        return filterRegistrationBean;
    }
}
