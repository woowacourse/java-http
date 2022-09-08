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

class RegisterApiControllerTest {

    @Test
    void registerApiHandler는_회원가입_요청을_처리할_수_있다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiController registerApiController = new RegisterApiController();
        // when
        boolean result = registerApiController.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void registerApiHandler는_회원가입이_아닌_요청은_처리할_수_없다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("POST /regist HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiController registerApiController = new RegisterApiController();
        // when
        boolean result = registerApiController.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void registerApiHandler는_POST가_아닌_회원가입_요청은_처리할_수_없다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("GET /regist HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterApiController registerApiController = new RegisterApiController();
        // when
        boolean result = registerApiController.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void registerApiHandler는_회원가입에_실패하면_INTERNAL_SERVER_ERROR를_반환한다() throws IOException {
        // given
        String body = "account=gugu&password=&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse httpResponse = new HttpResponse(outputStream);



        RegisterApiController registerApiController = new RegisterApiController();

        // when
        registerApiController.service(httpRequest, httpResponse);
        outputStream.close();

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(httpResponse.sendError());
    }

    @Test
    void registerApiHandler는_회원가입을_진행할_수_있다() throws IOException {
        // given
        String body = "account=gugo&password=password&email=hkkang@woowahan.com";
        String requestMessage = 회원가입_요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        Session session = new Session("sessionId");
        httpRequest.setSession(session);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse httpResponse = new HttpResponse(outputStream);



        RegisterApiController registerApiController = new RegisterApiController();

        // when
        registerApiController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(new HttpResponse(outputStream).found("/index.html ")
                        .addHeader("Set-Cookie", new Cookie(Map.of("JSESSIONID", session.getId())))
                        .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                        .addHeader("Content-Length", "0 "));
        outputStream.close();
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
