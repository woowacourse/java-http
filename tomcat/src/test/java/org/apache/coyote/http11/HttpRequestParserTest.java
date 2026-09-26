package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestParserTest {

    private final HttpRequestParser parser = new HttpRequestParser();

    @Test
    void 요청_라인과_헤더를_HTTP_요청으로_변환한다() throws IOException {
        final String rawRequest = String.join("\r\n",
                "GET /search?keyword=java%20http HTTP/1.1",
                "Host: localhost:8080",
                "X-Trace: first:second",
                "",
                ""
        );

        final HttpRequest request = parse(rawRequest);

        assertThat(request.httpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.path()).isEqualTo("/search");
        assertThat(request.version()).isEqualTo("HTTP/1.1");
        assertThat(request.headers())
                .containsEntry("host", "localhost:8080")
                .containsEntry("x-trace", "first:second");
        assertThat(request.params()).containsEntry("keyword", "java http");
        assertThat(request.body()).isEmpty();
    }

    @Test
    void 폼_요청의_본문을_읽어_파라미터로_변환한다() throws IOException {
        final String body = "account=gugu&password=pass%20word";
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body
        );

        final HttpRequest request = parse(rawRequest);

        assertThat(request.httpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.body()).isEqualTo(body);
        assertThat(request.params())
                .containsEntry("account", "gugu")
                .containsEntry("password", "pass word");
    }

    @Test
    void 본문이_Content_Length보다_짧으면_예외가_발생한다() {
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 10",
                "",
                "short"
        );

        assertThatThrownBy(() -> parse(rawRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content 길이");
    }

    private HttpRequest parse(final String rawRequest) throws IOException {
        return parser.parse(new BufferedReader(new StringReader(rawRequest)));
    }
}
