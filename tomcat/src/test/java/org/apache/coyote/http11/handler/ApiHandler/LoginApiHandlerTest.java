package org.apache.coyote.http11.handler.ApiHandler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.junit.jupiter.api.Test;

class LoginApiHandlerTest {

    @Test
    void loginApiHandler는_로그인_요청을_처리할_수_있다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginApiHandler loginApiHandler = new LoginApiHandler();

        // when
        final boolean result = loginApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void loginApiHandler는_로그인_요청이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /signin HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginApiHandler loginApiHandler = new LoginApiHandler();

        // when
        boolean result = loginApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void loginApiHandler는_로그인_요청이_POST가_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("GET /signin HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginApiHandler loginApiHandler = new LoginApiHandler();

        // when
        boolean result = loginApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void loginApiHandler는_로그인_처리_성공_시_302_index_html을_반환한다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginApiHandler loginApiHandler = new LoginApiHandler();

        // when
        ApiHandlerResponse response = loginApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.FOUND,
                        Map.of("Location", "/index.html "),
                        "",
                        ContentType.HTML)
                );
    }

    @Test
    void loginApiHandler는_존재하지않는_account가_요청되면_401_을_반환한다() throws IOException {
        // given
        final String body = "account=gogo&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginApiHandler loginApiHandler = new LoginApiHandler();

        // when
        ApiHandlerResponse response = loginApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.UNAUTHORIZED,
                        Map.of(),
                        "",
                        ContentType.HTML)
                );
    }

    @Test
    void loginApiHandler는_password가_일치하지_않으면_401_을_반환한다() throws IOException {
        // given
        final String body = "account=gugu&password=pwd";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginApiHandler loginApiHandler = new LoginApiHandler();

        // when
        ApiHandlerResponse response = loginApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.UNAUTHORIZED,
                        Map.of(),
                        "",
                        ContentType.HTML)
                );
    }

    private static String 로그인_요청_메시지(String requestLine, String body) {
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
