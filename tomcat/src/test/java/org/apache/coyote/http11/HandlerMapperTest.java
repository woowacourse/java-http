package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.coyote.http11.fixture.TestController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.mvc.HandlerMapper;
import org.apache.mvc.handlerchain.ControllerRequestHandlerChain;
import org.apache.mvc.handlerchain.NotFoundHandlerChain;
import org.apache.mvc.handlerchain.RequestHandlerChain;
import org.apache.mvc.handlerchain.StaticFileRequestHandlerChain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HandlerMapperTest {

    @Test
    void mapRequestToHandler() {
        // given
        RequestHandlerChain chain = new ControllerRequestHandlerChain(null, List.of(new TestController()));
        HandlerMapper mapper = new HandlerMapper(chain);

        String http = String.join("\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        // when

        HttpResponse response = mapper.mapToHandle(
                HttpRequest.parse(new ByteArrayInputStream(http.getBytes())));
        String responseString = response.getAsString();

        // then
        Assertions.assertAll(
                () -> assertThat(responseString).contains(HttpStatus.OK.getAsString()),
                () -> assertThat(responseString).contains("hello")
        );
    }

    @Test
    void mapToStaticFile() {
        // given
        HandlerMapper mapper = new HandlerMapper(new StaticFileRequestHandlerChain(null));

        String http = String.join("\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        // when
        HttpResponse response = mapper.mapToHandle(HttpRequest.parse(new ByteArrayInputStream(http.getBytes())));

        // then
        assertThat(response.getAsString()).contains(HttpStatus.OK.getAsString());
    }

    @Test
    void mapToNotFound() {
        // given
        HandlerMapper mapper = new HandlerMapper(new NotFoundHandlerChain());

        String http = String.join("\n",
                "GET /not-found HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        // when

        HttpResponse response = mapper.mapToHandle(HttpRequest.parse(new ByteArrayInputStream(http.getBytes())));

        // then
        assertThat(response.getAsString()).contains(HttpStatus.NOT_FOUND.name());
    }
}
