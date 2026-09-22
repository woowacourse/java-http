package org.apache.catalina.dispatcher.handler;

import org.apache.catalina.controller.RequestMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixture.get;

class HandlerMappingTest {

    private final Handler controllerHandler = new ControllerHandler(
            new RequestMapping(Map.of("/login.html", (request, response) -> "/login.html")));
    private final Handler staticHandler = new StaticHandler();
    private final HandlerMapping handlerMapping = new HandlerMapping(List.of(controllerHandler, staticHandler));

    @Test
    @DisplayName("정적 파일이 있어도 먼저 등록한 컨트롤러 핸들러가 처리한다.")
    void controllerFirst() throws IOException {
        assertThat(handlerMapping.findHandler(get("/login.html"))).containsSame(controllerHandler);
    }

    @Test
    @DisplayName("컨트롤러에 매핑되지 않은 정적 리소스는 정적 핸들러가 처리한다.")
    void fallbackToStatic() throws IOException {
        assertThat(handlerMapping.findHandler(get("/index.html"))).containsSame(staticHandler);
    }

    @Test
    @DisplayName("처리할 핸들러가 없으면 빈 값을 반환한다.")
    void noHandler() throws IOException {
        assertThat(handlerMapping.findHandler(get("/not-found"))).isEmpty();
    }

}
