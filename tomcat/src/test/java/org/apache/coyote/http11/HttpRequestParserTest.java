package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {
    private HttpRequestParser requestParser;
    private SessionManager sessionManager;

    public HttpRequestParserTest() {
        this.requestParser = HttpRequestParser.getInstance();
        this.sessionManager = SessionManager.getInstance();
    }

    @DisplayName("입력으로 들어온 값을 HttpRequest 객체로 반환한다")
    @Test
    void parseInput() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /robin/joon/genius HTTP/1.1",
                "Host: localhost",
                "Content-Length: 14",
                "",
                "name=robinjoon"
        );

        // when
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest parsedRequest = requestParser.parseInput(inputStream);

        // then
        assertAll(
                () -> assertThat(parsedRequest.hasMethod(HttpMethod.GET))
                        .isTrue(),
                () -> assertThat(parsedRequest.hasPath("/robin/joon/genius"))
                        .isTrue(),
                () -> assertThat(parsedRequest.getHeaderData("Host"))
                        .isEqualTo("localhost"),
                () -> assertThat(parsedRequest.getHeaderData("Content-Length"))
                        .isEqualTo("14"),
                () -> assertThat(parsedRequest.getBody())
                        .isEqualTo("name=robinjoon")
        );
    }

    @DisplayName("path에 쿼리스트링이 들어온 경우 파싱해서 저장한다")
    @Test
    void parseQueryString() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /robin?account=robin&password=password HTTP/1.1",
                "Host: localhost",
                "Content-Length: 0",
                ""
        );

        // when
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest parsedRequest = requestParser.parseInput(inputStream);

        // then
        assertAll(
                () -> assertThat(parsedRequest.hasQueryString())
                        .isTrue(),
                () -> assertThat(parsedRequest.getQueryData("account"))
                        .isEqualTo("robin"),
                () -> assertThat(parsedRequest.getQueryData("password"))
                        .isEqualTo("password")
        );
    }

    @DisplayName("Cookie는 따로 파싱해서 HttpCookie의 형태로 저장한다")
    @Test
    void parseCookie() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /robin HTTP/1.1",
                "Host: localhost",
                "Cookie: robin=genius; gugu=god",
                "Content-Length: 0",
                ""
        );

        // when
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest parsedRequest = requestParser.parseInput(inputStream);

        // then
        assertAll(
                () -> assertThat(parsedRequest.hasCookieFrom("robin"))
                        .isTrue(),
                () -> assertThat(parsedRequest.hasCookieFrom("gugu"))
                        .isTrue()
        );
    }

    @DisplayName("Cookie로 저장되지 않은 JSESSIONID가 전달될 경우 새로운 세션을 저장한다")
    @Test
    void parseSession() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /robin HTTP/1.1",
                "Host: localhost",
                "Cookie: JSESSIONID=1234qwer",
                "Content-Length: 0",
                ""
        );

        // when
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest parsedRequest = requestParser.parseInput(inputStream);

        // then
        String id = parsedRequest.getSession().getId();
        assertThat(sessionManager.findSession(id))
                .isNotNull();
    }

    @DisplayName("Cookie로 저장된 JSESSIONID가 전달될 경우 새로운 세션을 저장하지 않는다")
    @Test
    void withoutJSessionId() throws IOException {
        // given
        String expected = "1234qwer";
        String request = String.join("\r\n",
                "GET /robin HTTP/1.1",
                "Host: localhost",
                "Cookie: JSESSIONID=" + expected,
                "Content-Length: 0",
                ""
        );

        // when
        sessionManager.add(new Session(expected));
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest parsedRequest = requestParser.parseInput(inputStream);

        // then
        String actual = parsedRequest.getSession().getId();
        assertThat(actual)
                .isEqualTo(expected);
    }
}
