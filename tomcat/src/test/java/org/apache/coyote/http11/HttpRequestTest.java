package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP 요청")
class HttpRequestTest {

    @Nested
    @DisplayName("요청 본문 읽기")
    class RequestBodyReading {

        @Test
        @DisplayName("Content-Length만큼 본문을 읽는다")
        void readsBodyByContentLength() throws Exception {
            // given
            final var body = "account=gugu&password=password";
            final var reader = requestReader("Content-Length: " + body.length(), body);

            // when
            final var request = HttpRequest.readFrom(reader).orElseThrow();

            // then
            assertThat(request.body()).isEqualTo(body);
        }

        @Test
        @DisplayName("본문이 나누어 들어와도 Content-Length만큼 모두 읽는다")
        void readsBodyAcrossMultipleReads() throws Exception {
            // given
            final var body = "account=gugu";
            final var reader = new BufferedReader(new StringReader(
                    requestText("Content-Length: " + body.length(), body))) {
                @Override
                public int read(final char[] buffer, final int offset, final int length) throws IOException {
                    return super.read(buffer, offset, Math.min(length, 2));
                }
            };

            // when
            final var request = HttpRequest.readFrom(reader).orElseThrow();

            // then
            assertThat(request.body()).isEqualTo(body);
        }

        @Test
        @DisplayName("Content-Length가 없으면 빈 본문을 가진다")
        void hasEmptyBodyWithoutContentLength() throws Exception {
            // given
            final var reader = requestReader("Host: localhost:8080", "");

            // when
            final var request = HttpRequest.readFrom(reader).orElseThrow();

            // then
            assertThat(request.body()).isEmpty();
        }

        @Test
        @DisplayName("본문을 모두 읽기 전에 스트림이 끝나면 요청을 만들지 않는다")
        void rejectsTruncatedBody() throws Exception {
            // given
            final var reader = requestReader("Content-Length: 5", "data");

            // when
            final var request = HttpRequest.readFrom(reader);

            // then
            assertThat(request).isEmpty();
        }

        @ParameterizedTest(name = "{displayName} | Content-Length: {0}")
        @ValueSource(strings = {"invalid", "-1"})
        @DisplayName("Content-Length가 유효한 음이 아닌 정수가 아니면 요청을 만들지 않는다")
        void rejectsInvalidContentLength(final String contentLength) throws Exception {
            // given
            final var reader = requestReader("Content-Length: " + contentLength, "");

            // when
            final var request = HttpRequest.readFrom(reader);

            // then
            assertThat(request).isEmpty();
        }
    }

    private BufferedReader requestReader(final String header, final String body) {
        return new BufferedReader(new StringReader(requestText(header, body)));
    }

    private String requestText(final String header, final String body) {
        return String.join("\r\n",
                "POST /register HTTP/1.1",
                header,
                "",
                body);
    }
}
