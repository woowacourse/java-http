package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class IndexHandlerTest {

    private final HandlerMapping handlerMapping = new HandlerMapping(Map.of(
            "/", new IndexHandler()
    ));
    private final FrontController controller = new FrontController(handlerMapping);

    @Test
    @DisplayName("GET '/' 요청에 대한 응답이 정상적으로 처리된다.")
    void hello() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(controller, socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
