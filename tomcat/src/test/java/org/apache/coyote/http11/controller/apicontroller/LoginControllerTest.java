package org.apache.coyote.http11.controller.apicontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Cookie;
import org.apache.coyote.http11.session.Session;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

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

    @Test
    void loginApiController는_로그인_요청을_처리할_수_있다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginController loginController = new LoginController();

        // when
        final boolean result = loginController.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void loginApiController는_로그인_요청이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /signin HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginController loginController = new LoginController();

        // when
        boolean result = loginController.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void loginApiController는_로그인_요청이_POST가_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("GET /signin HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);

        final LoginController loginController = new LoginController();

        // when
        boolean result = loginController.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void loginApiController는_로그인_처리_성공_시_302_index_html을_반환한다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);
        Session session = new Session("sessionId");
        httpRequest.setSession(session);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse httpResponse = new HttpResponse(outputStream);

        LoginController loginController = new LoginController();

        // when
        loginController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(httpResponse.found("/index.html ")
                        .addHeader("Set-Cookie", new Cookie(Map.of("JSESSIONID", session.getId())))
                        .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                        .addHeader("Content-Length", "0 "));

        outputStream.close();
    }

    @Test
    void loginApiController는_존재하지않는_account가_요청되면_401_을_반환한다() throws IOException {
        // given
        final String body = "account=gogo&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final HttpResponse httpResponse = new HttpResponse(outputStream);

        LoginController loginController = new LoginController();

        // when
        loginController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(httpResponse.unAuthorized());

        outputStream.close();
    }

    @Test
    void loginApiController는_password가_일치하지않으면_401_을_반환한다() throws IOException {
        // given
        final String body = "account=gogo&password=invalidpassword";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse httpResponse = new HttpResponse(outputStream);

        LoginController loginController = new LoginController();

        // when
        loginController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(httpResponse.unAuthorized());

        outputStream.close();
    }

}
