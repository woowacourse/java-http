package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

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
                .isEqualTo("GET");

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
                .isEqualTo("POST");

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

}