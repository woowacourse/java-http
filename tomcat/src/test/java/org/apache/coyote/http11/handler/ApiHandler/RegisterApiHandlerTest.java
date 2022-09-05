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
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";
        String requestMessage = "POST /register HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: " + body.getBytes().length +  "\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Accept: */*\r\n"
                + "\r\n"
                + body;

        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();

        // when
        boolean result = registerApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void registerApiHandler는_회원가입이_아닌_요청은_처리할_수_없다() throws IOException {

        // given
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";
        String invalidRequestMessage = "POST /regist HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: " + body.getBytes().length +  "\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Accept: */*\r\n"
                + "\r\n"
                + body;
        InputStream inputStream = new ByteArrayInputStream(invalidRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();

        // when
        boolean result = registerApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void registerApiHandler는_POST가_아닌_회원가입_요청은_처리할_수_없다() throws IOException {

        // given
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";
        String invalidRequestMessage = "GET /register HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: " + body.getBytes().length +  "\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Accept: */*\r\n"
                + "\r\n"
                + body;

        InputStream inputStream = new ByteArrayInputStream(invalidRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();

        // when
        boolean result = registerApiHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }


    @Test
    void registerApiHandler는_회원가입을_진행할_수_있다() throws IOException {

        // given
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";
        String invalidRequestMessage = "POST /register HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: " + body.getBytes().length +  "\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Accept: */*\r\n"
                + "\r\n"
                + body;

        InputStream inputStream = new ByteArrayInputStream(invalidRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();

        // when
        ApiHandlerResponse response = (ApiHandlerResponse) registerApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.FOUND, Map.of("Location", "/index.html "), "", ContentType.HTML));
    }

    @Test
    void registerApiHandler는_회원가입에_실패하면_INTERNAL_SERVER_ERROR와_500_html을_반환한다() throws IOException {

        // given
        String body = "account=gugu&password=password&email=";
        String invalidRequestMessage = "POST /register HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: "+ body.getBytes().length +"\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Accept: */*\r\n"
                + "\r\n"
                + body;

        InputStream inputStream = new ByteArrayInputStream(invalidRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        RegisterApiHandler registerApiHandler = new RegisterApiHandler();

        // when
        ApiHandlerResponse response = (ApiHandlerResponse) registerApiHandler.getResponse(httpRequest);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ApiHandlerResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, Map.of(), "/500.html", ContentType.HTML));
    }
}
