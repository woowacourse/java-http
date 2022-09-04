package org.apache.mvc.handlerchain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.coyote.http11.fixture.TestController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class ControllerRequestHandlerChainTest {

    @Test
    void mapRequestToHandler() {
        // given
        ControllerRequestHandlerChain chain = new ControllerRequestHandlerChain(null, List.of(new TestController()));

        String http = String.join("\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        // when

        HttpResponse response = chain.handle(HttpRequest.parse(new ByteArrayInputStream(http.getBytes())), HttpResponse.initial());

        // then
        List<String> expected = List.of("HTTP/1.1 200 OK",
                "Content-Length: 5",
                "Content-Type: text/html;charset=utf-8",
                "hello");
        assertThat(response.getAsString()).contains(expected);
    }
}
