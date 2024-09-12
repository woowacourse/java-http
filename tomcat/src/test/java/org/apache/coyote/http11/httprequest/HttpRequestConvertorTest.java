package org.apache.coyote.http11.httprequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestMaker;

class HttpRequestConvertorTest {

    @DisplayName("빈 값이 들어오면 예외를 발생시킨다")
    @Test
    void throwExceptionWhenNull() {
        final String register = "";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        assertThatThrownBy(() -> HttpRequestConvertor.convertHttpRequest(bufferedReader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청이 비어 있습니다");
    }

    @DisplayName("RequestLine이 잘못 전달되면 예외를 발생시킨다")
    @Test
    void invalidRequestLine() {
        final String login = String.join("\r\n",
                "GET HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        InputStream inputStream = new ByteArrayInputStream(login.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        assertThatThrownBy(() -> HttpRequestConvertor.convertHttpRequest(bufferedReader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RequestLine이 잘못된 요청입니다");
    }

    @DisplayName("들어온 요청을 HttpRequest로 변환한다")
    @Test
    void convertHttpRequest() {
        String body = "account=gugu&password=1";
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(request);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getPath()).isEqualTo("/login"),
                () -> assertThat(httpRequest.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeaderValue("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpRequest.getHeaderValue("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(httpRequest.getHeaderValue(HttpHeaderName.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(httpRequest.getHeaderValue(HttpHeaderName.CONTENT_TYPE)).isEqualTo("application/x-www-form-urlencoded"),
                () -> assertThat(httpRequest.getBodyValue("account")).isEqualTo("gugu"),
                () -> assertThat(httpRequest.getBodyValue("password")).isEqualTo("1")
        );
    }

    @DisplayName("SessionManager에 저장된 세션이 쿠키로 들어오면 해당 세션을 불러온다")
    @Test
    void loadSession() {
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("abcdefg");
        sessionManager.add(session);
        String body = "account=gugu&password=1";
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=abcdefg",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(request);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getPath()).isEqualTo("/login"),
                () -> assertThat(httpRequest.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeaderValue("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpRequest.getHeaderValue("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(httpRequest.getHeaderValue(HttpHeaderName.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(httpRequest.getHeaderValue(HttpHeaderName.CONTENT_TYPE)).isEqualTo("application/x-www-form-urlencoded"),
                () -> assertThat(httpRequest.getBodyValue("account")).isEqualTo("gugu"),
                () -> assertThat(httpRequest.getBodyValue("password")).isEqualTo("1"),
                () -> assertThat(httpRequest.getSession().getId()).isEqualTo("abcdefg")
        );
    }

    @DisplayName("쿠키에 저장된 세션이 저장되지 않은 세션이면 새로 세션을 생성한다")
    @Test
    void createSession() {
        String body = "account=gugu&password=1";
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=abcdefg",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(request);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getPath()).isEqualTo("/login"),
                () -> assertThat(httpRequest.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeaderValue("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpRequest.getHeaderValue("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(httpRequest.getHeaderValue(HttpHeaderName.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(httpRequest.getHeaderValue(HttpHeaderName.CONTENT_TYPE)).isEqualTo("application/x-www-form-urlencoded"),
                () -> assertThat(httpRequest.getBodyValue("account")).isEqualTo("gugu"),
                () -> assertThat(httpRequest.getBodyValue("password")).isEqualTo("1"),
                () -> assertThat(httpRequest.getSession().getId()).isNotEqualTo("abcdefg")
        );
    }
}
