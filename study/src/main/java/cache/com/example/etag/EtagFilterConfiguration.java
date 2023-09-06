package cache.com.example.etag;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import java.util.Collections;
import java.util.List;
import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {

      final FilterRegistrationBean<ShallowEtagHeaderFilter> bean = new FilterRegistrationBean<>();
      final ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
      bean.setFilter(filter);
      bean.setUrlPatterns(List.of(
          "/etag",
          PREFIX_STATIC_RESOURCES + "/*"
      ));

      return bean;
    }
}
