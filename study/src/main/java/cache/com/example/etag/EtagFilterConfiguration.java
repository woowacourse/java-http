package cache.com.example.etag;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import cache.com.example.version.ResourceVersion;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    private final ResourceVersion version;

    public EtagFilterConfiguration(final ResourceVersion version) {
        this.version = version;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        final FilterRegistrationBean<ShallowEtagHeaderFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ShallowEtagHeaderFilter());
        registration.addUrlPatterns("/etag", PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/*");
        return registration;
    }

}
