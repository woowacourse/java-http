package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
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
import org.apache.catalina.http.startline.RequestUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("요청의 대상이 정적 파일이면 true를 반환한다.")
    @Test
    void isTargetStatic_true() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestUri("/index"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.isTargetStatic()).isTrue();
    }

    @DisplayName("요청의 대상이 정적 파일이 아니면 false를 반환한다.")
    @Test
    void isTargetStatic_false() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.isTargetStatic()).isFalse();
    }

    @DisplayName("요청 대상이 존재하지 않으면 true를 반환한다.")
    @Test
    void isTargetBlank_true() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestUri(""), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.isTargetBlank()).isTrue();
    }

    @DisplayName("요청 대상이 존재하면 false를 반환한다.")
    @Test
    void isTargetBlank_false() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.isTargetBlank()).isFalse();
    }

    @DisplayName("request uri가 주어진 문자로 시작하는지 아닌지 판별한다.")
    @Test
    void uriStartsWith() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/index"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.uriStartsWith("/ind")).isTrue();
        assertThat(request.uriStartsWith("/index/")).isFalse();
    }

    @DisplayName("쿠키에서 세션을 가져온다.")
    @Test
    void getSessionFromCookie() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
                new HttpHeaders(new HashMap<>(), new HttpCookies(Map.of("JSESSIONID", "session"))),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.getSessionFromCookie()).get().isEqualTo("session");
    }

    @DisplayName("쿠키에서 세션이 존재하지 않으면 Optional.empty()를 반환한다.")
    @Test
    void getSessionFromCookie_noSession() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.getSessionFromCookie()).isEmpty();
    }

    @DisplayName("요청의 HTTP 메서드를 반환한다.")
    @Test
    void getHttpMethod() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.POST);
    }

    @DisplayName("요청 uri의 path를 반환한다.")
    @Test
    void getTargetPath() throws URISyntaxException {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestUri("/index"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        Path expected = Path.of(getClass().getClassLoader().getResource("static/index.html").toURI());
        assertThat(request.getTargetPath()).isEqualTo(expected);
    }

    @DisplayName("요청 uri의 파일이 존재하지 않으면 예외를 던진다.")
    @Test
    void getTargetPath_targetNotFound() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestUri("/invalidUrl"), HttpVersion.HTTP11),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when&then
        assertThatThrownBy(request::getTargetPath)
                .isInstanceOf(CatalinaException.class);
    }

    @DisplayName("요청 body에서 특정 값을 꺼내온다.")
    @Test
    void getFromBody() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
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
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
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
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
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
                () -> assertThat(parsedRequest.isTargetBlank()).isFalse(),
                () -> assertThat(parsedRequest.isTargetStatic()).isFalse(),
                () -> assertThat(parsedRequest.uriStartsWith("/login")).isTrue(),
                () -> assertThat(parsedRequest.getHttpVersion()).isEqualTo(HttpVersion.HTTP11),
                () -> assertThat(parsedRequest.getSessionFromCookie()).get().isEqualTo("session"),
                () -> assertThat(parsedRequest.getFromBody("account")).isEqualTo("gugu"),
                () -> assertThat(parsedRequest.getFromBody("password")).isEqualTo("password")
        );
    }
}
