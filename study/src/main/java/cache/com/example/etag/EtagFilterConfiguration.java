package cache.com.example.etag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import cache.com.example.version.VersionHandlebarsHelper;

@Configuration
public class EtagFilterConfiguration {

    private final VersionHandlebarsHelper versionHandlebarsHelper;

    @Autowired
    public EtagFilterConfiguration(VersionHandlebarsHelper versionHandlebarsHelper) {
        this.versionHandlebarsHelper = versionHandlebarsHelper;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/etag");
        filterRegistrationBean.addUrlPatterns(versionHandlebarsHelper.staticUrls("/*", null));

        return filterRegistrationBean;
    }
}
