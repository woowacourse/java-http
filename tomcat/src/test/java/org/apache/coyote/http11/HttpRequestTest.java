package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void GET_요청의_RequestLine과_Header를_파싱한다() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        final HttpRequest request = HttpRequest.from(inputStream);

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("connection")).isEqualTo("keep-alive");
        assertThat(request.getHeader("Unknown")).isNull();
    }

    @Test
    void POST_요청의_Body와_Parameter를_파싱한다() throws IOException {
        // given
        final String requestBody = "account=gugu&password=password";

        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody
        );

        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        final HttpRequest request = HttpRequest.from(inputStream);

        // then
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getBody()).isEqualTo(requestBody);
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password"))
                .isEqualTo("password");
    }

    @Test
    void Query_String의_Parameter를_파싱한다() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /search?keyword=java&page=1 HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final InputStream inputStream = new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8)
        );

        // when
        final HttpRequest request = HttpRequest.from(inputStream);

        // then
        assertThat(request.getPath()).isEqualTo("/search");
        assertThat(request.getParameter("keyword")).isEqualTo("java");
        assertThat(request.getParameter("page")).isEqualTo("1");
    }

    @Test
    void Cookie의_JSESSIONID로_기존_Session을_조회한다() throws IOException {
        // given
        final String sessionId = UUID.randomUUID().toString();
        final Session existingSession = new Session(sessionId);

        SessionManager.getInstance().add(existingSession);

        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        );

        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        try {
            // when
            final HttpRequest request = HttpRequest.from(inputStream);

            final Session actual = request.getSession(false);

            // then
            assertThat(actual).isSameAs(existingSession);
        } finally {
            SessionManager.getInstance().remove(sessionId);
        }
    }

    @Test
    void 기존_Session이_없고_create가_false면_null을_반환한다() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        final HttpRequest request = HttpRequest.from(inputStream);

        final Session session = request.getSession(false);

        // then
        assertThat(session).isNull();
    }

    @Test
    void 기존_Session이_없고_create가_true면_새_Session을_생성한다() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        final HttpRequest request = HttpRequest.from(inputStream);

        // when
        final Session first = request.getSession(true);

        final Session second = request.getSession(true);

        try {
            // then
            assertThat(first).isNotNull();
            assertThat(second).isSameAs(first);

            assertThat(SessionManager.getInstance().findSession(first.getId()))
                    .isSameAs(first);
        } finally {
            if (first != null) {
                SessionManager.getInstance()
                        .remove(first.getId());
            }
        }
    }
}
