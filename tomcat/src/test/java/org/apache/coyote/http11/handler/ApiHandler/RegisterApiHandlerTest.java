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

class RegisterApiHandlerTest {

    @Test
    void registerApiHandler는_회원가입_요청을_처리할_수_있다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();
        // when
        boolean result = registerApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void registerApiHandler는_회원가입이_아닌_요청은_처리할_수_없다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("POST /regist HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();
        // when
        boolean result = registerApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void registerApiHandler는_POST가_아닌_회원가입_요청은_처리할_수_없다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("GET /regist HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();
        // when
        boolean result = registerApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void registerApiHandler는_회원가입을_진행할_수_있다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();

        // when
        ApiHandlerResponse response = (ApiHandlerResponse) registerApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.FOUND, Map.of("Location", "/index.html "), "",
                        ContentType.HTML));
    }

    @Test
    void registerApiHandler는_회원가입에_실패하면_INTERNAL_SERVER_ERROR를_반환한다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=";
        String requestMessage = 회원가입_요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();

        // when
        ApiHandlerResponse response = (ApiHandlerResponse) registerApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, Map.of(), "", ContentType.HTML));
    }

    private static String 회원가입_요청_메시지(String requestLine, String body) {
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
