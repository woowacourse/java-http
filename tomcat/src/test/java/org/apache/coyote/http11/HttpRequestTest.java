package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.exception.CatalinaException;
import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.body.HttpRequestBody;
import org.apache.catalina.http.header.HttpCookies;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpMethod;
import org.apache.catalina.http.startline.HttpRequestLine;
import org.apache.catalina.http.startline.HttpVersion;
import org.apache.catalina.http.startline.RequestURI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("요청의 대상이 정적 파일이면 true를 반환한다.")
    @Test
    void isURIStatic_true() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestURI("/index"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.isURIStatic()).isTrue();
    }

    @DisplayName("요청의 대상이 정적 파일이 아니면 false를 반환한다.")
    @Test
    void isURIStatic_false() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.isURIStatic()).isFalse();
    }

    @DisplayName("요청 대상이 home이면 true를 반환한다.")
    @Test
    void isURIHome_true() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestURI("/"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.isURIHome()).isTrue();
    }

    @DisplayName("요청 대상이 home이 아니면 false를 반환한다.")
    @Test
    void isURIHome_false() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.isURIHome()).isFalse();
    }

    @DisplayName("request uri가 주어진 문자로 시작하는지 아닌지 판별한다.")
    @Test
    void URIStartsWith() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/index"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.URIStartsWith("/ind")).isTrue();
        assertThat(request.URIStartsWith("/index/")).isFalse();
    }

    @DisplayName("쿠키에서 세션을 가져온다.")
    @Test
    void getSessionFromCookies() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(new HashMap<>(), new HttpCookies(Map.of("JSESSIONID", "session"))),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.getSessionFromCookies()).get().isEqualTo("session");
    }

    @DisplayName("쿠키에서 세션이 존재하지 않으면 Optional.empty()를 반환한다.")
    @Test
    void getSessionFromCookie_noSession() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.getSessionFromCookies()).isEmpty();
    }

    @DisplayName("요청의 HTTP 메서드를 반환한다.")
    @Test
    void getHttpMethod() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when&then
        assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.POST);
    }

    @DisplayName("요청 body에서 특정 값을 꺼내온다.")
    @Test
    void getFromBody() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody(Map.of("account", "gugu", "password", "password"))
        );

        // when&then
        assertThat(request.getFromBody("account")).isEqualTo("gugu");
        assertThat(request.getFromBody("password")).isEqualTo("password");
    }

    @DisplayName("요청 body에서 존재하지 않는 값을 꺼내오면 예외를 던진다.")
    @Test
    void getFromBody_keyNotFound() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody(Map.of("account", "gugu", "password", "password"))
        );

        // when&then
        assertThatThrownBy(() -> request.getFromBody("notKey"))
                .isInstanceOf(CatalinaException.class);
    }

    @DisplayName("요청의 HTTP 버전을 가져온다.")
    @Test
    void getHttpVersion() {
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody(Map.of("account", "gugu", "password", "password"))
        );

        // when&then
        assertThat(request.getHttpVersion()).isEqualTo(HttpVersion.HTTP11);
    }

    @DisplayName("InputStream을 request 형식으로 정상적으로 파싱한다.")
    @Test
    void parse() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Cookie: JSESSIONID=session",
                "",
                "account=gugu&password=password"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // then
        assertAll(
                () -> assertThat(parsedRequest.getHttpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(parsedRequest.isURIHome()).isFalse(),
                () -> assertThat(parsedRequest.isURIStatic()).isFalse(),
                () -> assertThat(parsedRequest.URIStartsWith("/login")).isTrue(),
                () -> assertThat(parsedRequest.getHttpVersion()).isEqualTo(HttpVersion.HTTP11),
                () -> assertThat(parsedRequest.getSessionFromCookies()).get().isEqualTo("session"),
                () -> assertThat(parsedRequest.getFromBody("account")).isEqualTo("gugu"),
                () -> assertThat(parsedRequest.getFromBody("password")).isEqualTo("password")
        );
    }
}
