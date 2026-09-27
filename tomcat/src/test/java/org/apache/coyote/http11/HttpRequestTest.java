package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void parsesQueryString() throws IOException {
        // given
        final var input = new ByteArrayInputStream(
                "GET /login?account=gugu&password=password HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8));

        // when
        final var request = HttpRequest.read(input);

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void readsFragmentedBodyByByteLengthAndDecodesParameters() throws IOException {
        // given
        final var body = "account=구구+test&password=p%2B%3D&email=hkkang%40woowahan.com";
        final var rawRequest = String.join("\r\n",
                "POST /register?account=ignored HTTP/1.1",
                "Host: localhost:8080",
                "content-length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body + "&extra=ignored");
        final var input = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public synchronized int read(final byte[] buffer, final int offset, final int length) {
                return super.read(buffer, offset, Math.min(length, 3));
            }
        };

        // when
        final var request = HttpRequest.read(input);

        // then
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/register");
        assertThat(request.getParameter("account")).isEqualTo("구구 test");
        assertThat(request.getParameter("password")).isEqualTo("p+=");
        assertThat(request.getParameter("email")).isEqualTo("hkkang@woowahan.com");
        assertThat(request.getParameter("extra")).isNull();
    }

    @Test
    void rejectsIncompleteBody() {
        final var input = new ByteArrayInputStream(
                "POST /login HTTP/1.1\r\nContent-Length: 20\r\n\r\naccount=gugu".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> HttpRequest.read(input)).isInstanceOf(IOException.class);
    }

    @Test
    void createsSessionOnlyWhenRequested() throws IOException {
        final var input = new ByteArrayInputStream("GET /login HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        final var request = HttpRequest.read(input);

        assertThat(request.getSession(false)).isNull();
        final var session = request.getSession(true);
        assertThat(request.getSession(false)).isSameAs(session);
        assertThat(request.getSession(true)).isSameAs(session);
        session.invalidate();
    }
}
