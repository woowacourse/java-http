package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import java.util.Optional;
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
import org.apache.coyote.http11.session.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("[GET] 로그인 페이지 (/login)")
    @Test
    void getLogin() {
        LoginController loginController = new LoginController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/login");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());

        HttpResponse httpResponse = loginController.doGet(
                new HttpRequest(requestLine, requestHeaders, RequestBody.ofEmpty()));

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(View.LOGIN.getContents()),
                () -> assertThat(httpResponse.getContentType()).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8)
        );
    }

    @DisplayName("[POST] 로그인 페이지 (/login) + 성공")
    @Test
    void postLogin() {
        LoginController loginController = new LoginController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/login");
        RequestLine requestLine = new RequestLine(Method.POST, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());
        RequestBody requestBody = RequestBody.of("account=gugu&password=password");
        HttpResponse httpResponse = loginController.doPost(
                new HttpRequest(requestLine, requestHeaders, requestBody));

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getLocation()).isEqualTo(View.INDEX.getViewFileName())
        );
    }

    @DisplayName("[POST] 로그인 페이지 (/login) + 실패")
    @Test
    void postLoginInvalidAccount() {
        LoginController loginController = new LoginController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/login");
        RequestLine requestLine = new RequestLine(Method.POST, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());
        RequestBody requestBody = RequestBody.of("account=gugu&password=123212");
        HttpResponse httpResponse = loginController.doPost(
                new HttpRequest(requestLine, requestHeaders, requestBody));

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getLocation()).isEqualTo(View.UNAUTHORIZED.getViewFileName())
        );
    }

    @DisplayName("[POST] 로그인 페이지 (/login) + 이미 로그인한 경우")
    @Test
    void postLoginAlready() {
        LoginController loginController = new LoginController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/login");
        RequestLine requestLine = new RequestLine(Method.POST, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());
        RequestBody requestBody = RequestBody.of("account=gugu&password=password");
        HttpResponse httpResponse = loginController.doPost(
                new HttpRequest(requestLine, requestHeaders, requestBody));

        Cookie cookie = httpResponse.getCookie();
        Optional<String> jSession = cookie.getJSessionValue();

        RequestHeaders requestHeaders2 = new RequestHeaders(Map.of("Cookie", Cookie.JSESSIONID + "=" + jSession.get()));
        HttpResponse httpResponse2 = loginController.doGet(
                new HttpRequest(requestLine, requestHeaders2, requestBody));

        assertAll(
                () -> assertThat(httpResponse2.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse2.getLocation()).isEqualTo(View.INDEX.getViewFileName())
        );
    }
}
