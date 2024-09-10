package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.apache.coyote.http11.startline.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

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
        assertThat(parsedRequest).isNotNull();
    }

    @DisplayName("요청의 대상이 정적 파일이면 true를 반환한다.")
    @Test
    void isTargetStatic_true() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.isTargetStatic()).isTrue();
    }

    @DisplayName("요청의 대상이 정적 파일이 아니면 false를 반환한다.")
    @Test
    void isTargetStatic_false() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.isTargetStatic()).isFalse();
    }

    @DisplayName("요청 대상이 존재하지 않으면 true를 반환한다.")
    @Test
    void isTargetBlank_true() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET  HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.isTargetBlank()).isTrue();
    }

    @DisplayName("요청 대상이 존재하면 false를 반환한다.")
    @Test
    void isTargetBlank_false() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.isTargetBlank()).isFalse();
    }

    @DisplayName("request uri가 주어진 문자로 시작하는지 아닌지 판별한다.")
    @Test
    void uriStartsWith() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.uriStartsWith("/ind")).isTrue();
        assertThat(parsedRequest.uriStartsWith("/index/")).isFalse();
    }

    @DisplayName("쿠키에서 세션을 가져온다.")
    @Test
    void getSessionFromCookie() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=session"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.getSessionFromCookie().get()).isEqualTo("session");
    }

    @DisplayName("쿠키에서 세션이 존재하지 않으면 Optional.empty()를 반환한다.")
    @Test
    void getSessionFromCookie_noSession() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.getSessionFromCookie()).isEmpty();
    }

    @DisplayName("요청의 HTTP 메서드를 반환한다.")
    @Test
    void getHttpMethod() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.getHttpMethod()).isEqualTo(HttpMethod.POST);
    }

    @DisplayName("요청 uri의 path를 반환한다.")
    @Test
    void getTargetPath() throws IOException, URISyntaxException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        Path expected = Path.of(getClass().getClassLoader().getResource("static/index.html").toURI());
        assertThat(parsedRequest.getTargetPath()).isEqualTo(expected);
    }

    @DisplayName("요청 uri의 파일이 존재하지 않으면 예외를 던진다.")
    @Test
    void getTargetPath_targetNotFound() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /india HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThatThrownBy(parsedRequest::getTargetPath)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("요청 body에서 특정 값을 꺼내온다.")
    @Test
    void getFromBody() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.getFromBody("account")).isEqualTo("gugu");
        assertThat(parsedRequest.getFromBody("password")).isEqualTo("password");
    }

    @DisplayName("요청 body에서 존재하지 않는 값을 꺼내오면 예외를 던진다.")
    @Test
    void getFromBody_keyNotFound() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThatThrownBy(() -> parsedRequest.getFromBody("notKey"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("요청의 HTTP 버전을 가져온다.")
    @Test
    void getHttpVersion() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest parsedRequest = HttpRequest.parse(inputStream);

        // when&then
        assertThat(parsedRequest.getHttpVersion()).isEqualTo("HTTP/1.1");
    }
}
