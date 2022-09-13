package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import nextstep.jwp.view.View;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.URI;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("[GET] 회원가입 페이지 (/register)")
    @Test
    void getRegister() {
        RegisterController registerController = new RegisterController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/register");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());

        HttpResponse httpResponse = registerController.doGet(
                new HttpRequest(requestLine, requestHeaders, RequestBody.ofEmpty()));

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(View.REGISTER.getContents()),
                () -> assertThat(httpResponse.getContentType()).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8)
        );
    }

    @DisplayName("[POST] 회원가입 (/register)")
    @Test
    void postRegister() {
        RegisterController registerController = new RegisterController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/register");
        RequestLine requestLine = new RequestLine(Method.POST, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());

        HttpResponse httpResponse = registerController.doPost(
                new HttpRequest(requestLine, requestHeaders,
                        RequestBody.of("account=lala&email=lala@gmail.com&password=1234")));

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getLocation()).isEqualTo(View.INDEX.getViewFileName())
        );
    }

    @DisplayName("[POST] 회원가입 (/register) + 이미 존재하는 계정")
    @Test
    void postRegisterExist() {
        RegisterController registerController = new RegisterController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/register");
        RequestLine requestLine = new RequestLine(Method.POST, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());

        registerController.doPost(
                new HttpRequest(requestLine, requestHeaders,
                        RequestBody.of("account=hoho&email=hoho@gmail.com&password=1234")));

        HttpResponse httpResponse = registerController.doPost(
                new HttpRequest(requestLine, requestHeaders,
                        RequestBody.of("account=hoho&email=hoho@gmail.com&password=1234")));

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getLocation()).isEqualTo(View.UNAUTHORIZED.getViewFileName())
        );
    }
}
