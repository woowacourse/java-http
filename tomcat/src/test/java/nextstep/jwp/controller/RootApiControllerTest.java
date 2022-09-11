package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.Test;

class RootApiControllerTest {

    @Test
    void rootApiHandler는_root요청_처리_시_helloWorld를_반환한다() throws Exception {
        // given
        String body = "";
        String requestMessage = 루트_요청_메시지("GET / HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        RootApiController registerApiController = new RootApiController();

        // when
        registerApiController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(httpResponse.ok(ContentType.HTML, "Hello world!"));
    }

    @Test
    void rootApiHandler는_Post요청이_들어오면_예외를_던진다() throws Exception {
        // given
        String body = "";
        String requestMessage = 루트_요청_메시지("POST / HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        RootApiController rootApiController = new RootApiController();

        // when
        assertThatThrownBy(() -> rootApiController.service(httpRequest, httpResponse))
                .isInstanceOf(InvocationTargetException.class);
    }

    private String 루트_요청_메시지(String requestLine, String body) {
        return String.join("\r\n",
                requestLine,
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);
    }

    private HttpRequest httpRequest_생성(String requestMessage) throws IOException {
        HttpRequest httpRequest;
        try (InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            httpRequest = HttpRequest.of(bufferedReader);
        }
        return httpRequest;
    }
}
