package cache.com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        // FilterRegistrationBean: 필터 등록 역할. ShallowEtagHeaderFilter 필터 등록하여 ETag 자동 생성 및 관리
        // ShallowEtagHeaderFilter: 리소스의 내용을 읽어 간단한(얕은) 해시를 만들어 ETag 헤더 설정
        // 실행 시점: Spring Boot 띄울 때
        ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
        FilterRegistrationBean<ShallowEtagHeaderFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.addUrlPatterns("/etag");
        return registrationBean;
    }
}
