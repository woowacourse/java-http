package org.apache.coyote.http11.request;

import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;

import java.nio.charset.StandardCharsets;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void GET_요청을_파싱한다() throws Exception {
        // given
        final String rawRequest =
                String.join(
                        "\r\n",
                        "GET /index.html HTTP/1.1",
                        "Host: localhost:8080",
                        "Cookie: JSESSIONID=session-id",
                        "",
                        ""
                );

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(
                        rawRequest.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        // when
        final HttpRequest request =
                HttpRequest.from(inputStream)
                        .orElseThrow();

        // then
        assertThat(request.getMethod())
                .isEqualTo(HttpMethod.GET);

        assertThat(request.getPath())
                .isEqualTo("/index.html");

        assertThat(
                request.getProtocolVersion()
        ).isEqualTo("HTTP/1.1");

        assertThat(
                request.getHeader("Host")
        ).hasValue(
                "localhost:8080"
        );

        assertThat(
                request.getCookie("JSESSIONID")
        ).hasValue(
                "session-id"
        );
    }

    @Test
    void POST_요청의_Body와_파라미터를_파싱한다()
            throws Exception {

        // given
        final String body =
                "account=moca"
                        + "&password=1234"
                        + "&email=moca%40email.com";

        final String rawRequest =
                String.join(
                        "\r\n",
                        "POST /register HTTP/1.1",
                        "Host: localhost:8080",
                        "Content-Length: "
                                + body.getBytes(
                                StandardCharsets.UTF_8
                        ).length,
                        "Content-Type: application/x-www-form-urlencoded",
                        "",
                        body
                );

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(
                        rawRequest.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        // when
        final HttpRequest request =
                HttpRequest.from(inputStream)
                        .orElseThrow();

        // then
        assertThat(request.getMethod())
                .isEqualTo(HttpMethod.POST);

        assertThat(request.getPath())
                .isEqualTo("/register");

        assertThat(request.getBody())
                .isEqualTo(body);

        assertThat(
                request.getParameter("account")
        ).hasValue("moca");

        assertThat(
                request.getParameter("password")
        ).hasValue("1234");

        assertThat(
                request.getParameter("email")
        ).hasValue(
                "moca@email.com"
        );
    }

    @Test
    void GET_요청의_Query_String_파라미터를_파싱한다() throws Exception {

        // given
        final String rawRequest = String.join(
                "\r\n",
                "GET /search?keyword=hello%20world&page=2 HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        final HttpRequest request = HttpRequest.from(inputStream).orElseThrow();

        // then
        assertThat(request.getPath()).isEqualTo("/search");
        assertThat(request.getParameter("keyword")).hasValue("hello world");
        assertThat(request.getParameter("page")).hasValue("2");
    }

    @Test
    void JSESSIONID로_기존_세션을_조회한다() throws Exception {
        // given
        final HttpSession session = sessionManager.createSession();
        final HttpRequest request = createSessionRequest("Cookie: JSESSIONID=" + session.getId());

        try {
            // when
            final HttpSession foundSession = request.getSession(false);

            // then
            assertThat(foundSession).isSameAs(session);
            assertThat(request.getNewSession()).isEmpty();
        } finally {
            session.invalidate();
        }
    }

    @Test
    void 세션이_없으면_getSession_false는_null을_반환한다() throws Exception {
        // given
        final HttpRequest request = createSessionRequest("Cookie: JSESSIONID=unknown-session");

        // when & then
        assertThat(request.getSession(false)).isNull();
        assertThat(request.getNewSession()).isEmpty();
    }

    @Test
    void 세션이_없으면_getSession은_새_세션을_생성한다() throws Exception {
        // given
        final HttpRequest request = createSessionRequest("Host: localhost:8080");

        // when
        final HttpSession session = request.getSession();

        // then
        try {
            assertThat(request.getNewSession()).containsSame(session);
            assertThat(request.getSession()).isSameAs(session);
            assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
        } finally {
            session.invalidate();
        }
    }

    @Test
    void 세션을_무효화한_뒤_getSession을_호출하면_새_세션을_생성한다() throws Exception {
        // given
        final HttpSession existingSession = sessionManager.createSession();
        final HttpRequest request =
                createSessionRequest("Cookie: JSESSIONID=" + existingSession.getId());

        request.getSession(false).invalidate();

        // when
        final HttpSession newSession = request.getSession();

        // then
        try {
            assertThat(newSession.getId()).isNotEqualTo(existingSession.getId());
            assertThat(request.getNewSession()).containsSame(newSession);
        } finally {
            newSession.invalidate();
        }
    }

    private HttpRequest createSessionRequest(final String header) throws Exception {
        final String rawRequest = String.join(
                "\r\n",
                "GET /login HTTP/1.1",
                header,
                "",
                ""
        );

        final HttpRequest request = HttpRequest.from(
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8))
        ).orElseThrow();

        request.setSessionManager(sessionManager);

        return request;
    }
}
