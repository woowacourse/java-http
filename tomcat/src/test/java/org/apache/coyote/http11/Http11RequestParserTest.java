package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.TextBody;
import org.junit.jupiter.api.Test;

class Http11RequestParserTest {

    @Test
    void 바디가_있는_요청을_파싱한다() throws IOException {
        final String body = "{\"name\":\"김\"}";
        final HttpServletRequest request = parse(String.join("\r\n",
                "POST /users?id=1 HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/json",
                "Content-Length: " + utf8Length(body),
                "",
                body));

        assertThat(request.requestLine().getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.requestLine().getUri().getPath()).isEqualTo("/users");
        assertThat(request.requestLine().getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(request.requestLine().getQueryParam().get("id")).contains("1");
        assertThat(request.headers().get("Host")).contains("localhost:8080");
        assertThat(request.body()).isEqualTo(new TextBody(body));
    }

    @Test
    void 바디가_없는_요청을_파싱한다() throws IOException {
        final HttpServletRequest request = parse(String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""));

        assertThat(request.requestLine().getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.body()).isEqualTo(new TextBody(""));
    }

    private HttpServletRequest parse(final String rawRequest) throws IOException {
        final InputStream inputStream =
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));
        return new Http11RequestParser(inputStream).parse();
    }

    private int utf8Length(final String body) {
        return body.getBytes(StandardCharsets.UTF_8).length;
    }
}
