package org.apache.coyote.http11.handler.ApiHandler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.junit.jupiter.api.Test;

class RootApiHandlerTest {

    @Test
    void rootApiHandler는_root요청을_처리할_수_있다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 루트_요청_메시지("GET / HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RootApiHandler rootApiHandler = new RootApiHandler();

        // when
        boolean result = rootApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void rootApiHandler는_root요청이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "";
        String invalidRequestMessage = 루트_요청_메시지("GET /index.html HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(invalidRequestMessage);

        RootApiHandler rootApiHandler = new RootApiHandler();

        // when
        boolean result = rootApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void rootApiHandler는_root요청이_GET이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "";
        String invalidRequestMessage = 루트_요청_메시지("POST /index.html HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(invalidRequestMessage);

        RootApiHandler rootApiHandler = new RootApiHandler();

        // when
        boolean result = rootApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void rootApiHandler는_root요청_처리_시_helloWorld를_반환한다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 루트_요청_메시지("GET / HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RootApiHandler rootApiHandler = new RootApiHandler();

        // when
        ApiHandlerResponse response = rootApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.OK, new LinkedHashMap<>(), "Hello world!", ContentType.HTML));
    }

    private static String 루트_요청_메시지(String requestLine, String body) {
        return String.join("\r\n",
                requestLine,
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);
    }

    private static HttpRequest httpRequest_생성(String requestMessage) throws IOException {
        HttpRequest httpRequest;
        try (InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            httpRequest = HttpRequest.of(bufferedReader);
        }
        return httpRequest;
    }
}
