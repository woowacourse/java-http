package org.apache.catalina.dispatcher;

import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.dispatcher.handler.ControllerHandler;
import org.apache.catalina.dispatcher.handler.HandlerMapping;
import org.apache.catalina.dispatcher.handler.StaticHandler;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixture.get;

class DispatcherTest {

    private final Dispatcher dispatcher = new Dispatcher(
            new HandlerMapping(List.of(
                    new ControllerHandler(new RequestMapping(Map.of("/hello", (request, response) -> "redirect:/index.html"))),
                    new StaticHandler())),
            new ViewResolver());

    @Test
    @DisplayName("컨트롤러가 반환한 뷰 이름으로 응답한다.")
    void dispatchToController() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        dispatcher.dispatch(get("/hello"), response);

        // then
        assertThat(write(response))
                .startsWith("HTTP/1.1 302 FOUND\r\n")
                .contains("Location: /index.html\r\n");
    }

    @Test
    @DisplayName("정적 리소스 요청은 200으로 응답한다.")
    void dispatchToStatic() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        dispatcher.dispatch(get("/index.html"), response);

        // then
        assertThat(write(response)).startsWith("HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=utf-8\r\n");
    }

    @Test
    @DisplayName("처리할 핸들러가 없으면 404로 응답한다.")
    void dispatchNotFound() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        dispatcher.dispatch(get("/not-found"), response);

        // then
        assertThat(write(response)).startsWith("HTTP/1.1 404 Not Found\r\n");
    }

    private String write(HttpResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.write(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

}
