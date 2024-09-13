package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class NotFoundHandlerTest {

    private final HandlerMapping handlerMapping = new HandlerMapping(Map.of(
            "/", new IndexHandler()
    ));
    private final FrontController controller = new FrontController(handlerMapping);

    @Test
    @DisplayName("존재하지 않는 GET 매핑일 경우 404 응답이 반환된다.")
    void getNotFound() throws Exception {
        // given
        String httpRequest = String.join("\r\n",
                "GET /not-found-url HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(controller, socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())
                ));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 GET 매핑일 경우 404 응답이 반환된다.")
    void postNotFound() throws Exception {
        // given
        String httpRequest = String.join("\r\n",
                "POST /not-found-url HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(controller, socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())
                ));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
