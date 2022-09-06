package org.apache.coyote.http11.handler.FileHandler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.junit.jupiter.api.Test;

class FileHandlerTest {

    @Test
    void FileHandler는_file요청을_처리할_수_있다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 파일_요청_메시지("GET /index.html HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        FileHandler fileHandler = new FileHandler();

        // when
        boolean result = fileHandler.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void FileHandler는_file요청이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 파일_요청_메시지("GET /index HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        FileHandler fileHandler = new FileHandler();

        // when
        boolean result = fileHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void FileHandler는_file요청_처리_시_file의_path를_반환한다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 파일_요청_메시지("GET /index.html HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        FileHandler fileHandler = new FileHandler();

        // when
        FileHandlerResponse response = fileHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new FileHandlerResponse(HttpStatus.OK, httpRequest.getPath()));
    }

    private static String 파일_요청_메시지(String requestLine, String body) {
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
