package org.apache.coyote.http11;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    @DisplayName("요청 라인, 헤더, 쿠키를 파싱한다")
    void parsesRequestLineAndHeaders() throws Exception {
        final String rawRequest = String.join("\r\n",
                "GET /index.html?name=gugu HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: text/plain",
                "cookie: JSESSIONID=session-1",
                "",
                "");

        final HttpRequest request = new HttpRequest(
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getUri()).isEqualTo("/index.html?name=gugu");
        assertThat(request.getHttpVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.getHeader("host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("content-type")).isEqualTo("text/plain");
        assertThat(request.getCookie("JSESSIONID")).isEqualTo("session-1");
        assertThat(request.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Content-Length에 지정된 바이트만큼 본문을 읽는다")
    void readsRequestBodyByContentLengthBytes() throws Exception {
        final String body = "name=이산";
        final HttpRequest request = new HttpRequest(new ByteArrayInputStream(createRequest(body)));

        assertThat(request.getBody()).isEqualTo(body);
    }

    @Test
    @DisplayName("입력 스트림이 일부 바이트만 반환해도 본문을 끝까지 읽는다")
    void keepsReadingWhenInputReturnsFewerBytesThanRequested() throws Exception {
        final String body = "account=gugu";
        final InputStream input = new LimitedReadInputStream(new ByteArrayInputStream(createRequest(body)), 2);

        final HttpRequest request = new HttpRequest(input);

        assertThat(request.getBody()).isEqualTo(body);
    }

    @Test
    @DisplayName("Content-Length보다 본문이 짧으면 EOF 예외를 던진다")
    void throwsEofWhenRequestBodyEndsBeforeContentLength() {
        final String body = "short";
        final byte[] fullRequest = createRequest(body + "-tail");
        final String requestText = new String(fullRequest, StandardCharsets.ISO_8859_1);
        final int bodyStart = requestText.indexOf("\r\n\r\n") + 4;
        final byte[] incompleteRequest = Arrays.copyOf(fullRequest, bodyStart + body.length());

        assertThatThrownBy(() -> new HttpRequest(new ByteArrayInputStream(incompleteRequest)))
                .isInstanceOf(EOFException.class);
    }

    @Test
    @DisplayName("숫자가 아닌 Content-Length를 거부한다")
    void rejectsNonNumericContentLength() {
        assertThatThrownBy(() -> new HttpRequest(new ByteArrayInputStream(createRequest("abc", "invalid"))))
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("음수 Content-Length를 거부한다")
    void rejectsNegativeContentLength() {
        assertThatThrownBy(() -> new HttpRequest(new ByteArrayInputStream(createRequest("", "-1"))))
                .isInstanceOf(IOException.class);
    }

    private byte[] createRequest(final String body) {
        final int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
        return createRequest(body, String.valueOf(contentLength));
    }

    private byte[] createRequest(final String body, final String contentLength) {
        final String rawRequest = String.join("\r\n",
                "POST /submit HTTP/1.1",
                "Content-Length: " + contentLength,
                "",
                body);
        return rawRequest.getBytes(StandardCharsets.UTF_8);
    }

    private static class LimitedReadInputStream extends FilterInputStream {

        private final int maxReadLength;

        private LimitedReadInputStream(final InputStream input, final int maxReadLength) {
            super(input);
            this.maxReadLength = maxReadLength;
        }

        @Override
        public int read(final byte[] bytes, final int offset, final int length) throws IOException {
            return super.read(bytes, offset, Math.min(length, maxReadLength));
        }
    }
}
