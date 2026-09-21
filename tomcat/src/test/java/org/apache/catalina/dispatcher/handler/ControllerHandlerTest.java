package org.apache.catalina.dispatcher.handler;

import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixture.get;

class ControllerHandlerTest {

    private final ControllerHandler controllerHandler = new ControllerHandler(
            new RequestMapping(Map.of("/login", (request, response) -> "/login.html")));

    @Test
    @DisplayName("매핑된 경로면 처리한다.")
    void supports() throws IOException {
        assertThat(controllerHandler.supports(get("/login"))).isTrue();
    }

    @Test
    @DisplayName("쿼리 스트링이 있어도 경로로 판단한다.")
    void supportsWithQueryString() throws IOException {
        assertThat(controllerHandler.supports(get("/login?account=gugu"))).isTrue();
    }

    @Test
    @DisplayName("매핑되지 않은 경로면 처리하지 않는다.")
    void notSupports() throws IOException {
        assertThat(controllerHandler.supports(get("/unknown"))).isFalse();
    }

    @Test
    @DisplayName("컨트롤러가 반환한 뷰 이름을 그대로 반환한다.")
    void handle() throws IOException {
        assertThat(controllerHandler.handle(get("/login"), new HttpResponse())).isEqualTo("/login.html");
    }

}
