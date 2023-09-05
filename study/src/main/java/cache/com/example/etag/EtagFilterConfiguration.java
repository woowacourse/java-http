package cache.com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

	@Bean
	public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
		final ShallowEtagHeaderFilter shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
		final FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean
			= new FilterRegistrationBean<>(shallowEtagHeaderFilter);
		filterRegistrationBean.addUrlPatterns("/etag");
		return filterRegistrationBean;
	}
}
