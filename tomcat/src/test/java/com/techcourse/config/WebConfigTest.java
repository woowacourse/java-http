package com.techcourse.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.apache.catalina.routing.RequestHandler;
import org.apache.catalina.routing.RequestRegistry;
import org.apache.catalina.routing.RouteKey;
import org.apache.coyote.http11.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WebConfigTest {

    @Test
    @DisplayName("컨트롤러의 요청 매핑을 등록하고 인스턴스 메서드를 호출한다")
    void registersControllerRoutes() throws ReflectiveOperationException {
        final Method requestMapping = WebConfig.class.getDeclaredMethod("requestMapping");
        requestMapping.setAccessible(true);
        final RequestRegistry registry = (RequestRegistry) requestMapping.invoke(new WebConfig());

        final RequestHandler greeting = registry.getHandler(new RouteKey(HttpMethod.GET, "/")).orElseThrow();
        assertThat(greeting.handle(null, null)).isEqualTo("hello world");
        assertThat(registry.getHandler(new RouteKey(HttpMethod.GET, "/login"))).isPresent();
        assertThat(registry.getHandler(new RouteKey(HttpMethod.POST, "/register"))).isPresent();
    }
}
