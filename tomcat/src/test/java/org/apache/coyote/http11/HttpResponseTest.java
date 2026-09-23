package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP 응답")
class HttpResponseTest {

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
