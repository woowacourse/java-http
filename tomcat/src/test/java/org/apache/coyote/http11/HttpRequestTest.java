package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 영문_본문_읽기() throws IOException {
        String body = "name=daegil";

        HttpRequest request = HttpRequest.from(new ByteArrayInputStream(requestBytes(body)));

        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getParameter("name")).isEqualTo("daegil");
    }

    @Test
    void 한글_본문을_바이트_길이_기준으로_읽기() throws IOException {
        String body = "name=대길";

        HttpRequest request = HttpRequest.from(new ByteArrayInputStream(requestBytes(body)));

        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getParameter("name")).isEqualTo("대길");
    }

    @Test
    void 나누어_전달된_본문_읽기() throws IOException {
        String body = "name=대길";
        var input = new ByteArrayInputStream(requestBytes(body)) {
            @Override
            public synchronized int read(byte[] buffer, int offset, int length) {
                return super.read(buffer, offset, Math.min(length, 2));
            }
        };

        HttpRequest request = HttpRequest.from(input);

        assertThat(request.getBody()).isEqualTo(body);
    }

    @Test
    void 지정된_길이보다_짧은_본문은_예외_발생() {
        byte[] bytes = ("POST /register HTTP/1.1\r\nContent-Length: 20\r\n\r\nname=대길")
                .getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> HttpRequest.from(new ByteArrayInputStream(bytes)))
                .isInstanceOf(EOFException.class);
    }

    @Test
    void 본문_이후의_바이트는_읽지_않고_유지() throws IOException {
        String body = "name=대길";
        String request = new String(requestBytes(body), StandardCharsets.UTF_8);
        var input = new ByteArrayInputStream((request + "NEXT").getBytes(StandardCharsets.UTF_8));

        HttpRequest parsed = HttpRequest.from(input);

        assertThat(parsed.getBody()).isEqualTo(body);
        assertThat(new String(input.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("NEXT");
    }

    @Test
    void 본문_없는_요청_읽기() throws IOException {
        var input = new ByteArrayInputStream("GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n"
                .getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.from(input);

        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getHeader("Host")).isEqualTo("localhost");
        assertThat(request.getBody()).isEmpty();
    }

    @Test
    void 빈_입력은_null_반환() throws IOException {
        assertThat(HttpRequest.from(new ByteArrayInputStream(new byte[0]))).isNull();
    }

    private byte[] requestBytes(String body) {
        return ("POST /register HTTP/1.1\r\nContent-Length: "
                + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" + body)
                .getBytes(StandardCharsets.UTF_8);
    }
}
