package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP 응답")
class HttpResponseTest {

    @Test
    @DisplayName("상태선, 헤더, 본문으로 구성된 HTTP 응답을 출력한다")
    void writesHttpResponse() throws Exception {
        // given
        final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        final var response = HttpResponse.ok(new ResponseContent("text/plain;charset=utf-8", body))
                .addHeader("X-Test", "value");
        final var outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "X-Test: value ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"));
    }

    @Nested
    @DisplayName("리다이렉트 응답")
    class RedirectResponseTests {

        @Test
        @DisplayName("302 Found 상태를 가진다")
        void hasFoundStatus() {
            // given
            final var location = "/index.html";

            // when
            final var response = HttpResponse.redirect(location);

            // then
            assertThat(response.status()).isEqualTo(HttpStatus.FOUND);
        }

        @Test
        @DisplayName("이동할 경로를 Location 헤더에 가진다")
        void hasLocationHeader() {
            // given
            final var location = "/index.html";

            // when
            final var response = HttpResponse.redirect(location);

            // then
            assertThat(response.headers().firstValue("Location")).contains(location);
        }

        @Test
        @DisplayName("본문이 비어 있다")
        void hasEmptyBody() {
            // given
            final var location = "/index.html";

            // when
            final var response = HttpResponse.redirect(location);

            // then
            assertThat(response.content().body()).isEmpty();
        }
    }
}
