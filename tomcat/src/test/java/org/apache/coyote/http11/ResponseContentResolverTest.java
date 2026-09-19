package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("정적 리소스 응답 선택")
class ResponseContentResolverTest {

    private final ResponseContentResolver resolver = new ResponseContentResolver();

    @Test
    @DisplayName("/login 요청은 로그인 페이지의 내용과 HTML 타입을 반환한다")
    void loginPathResolvesToLoginPage() throws IOException {
        // given
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();

            // when
            final var response = resolver.resolve("/login");

            // then
            assertThat(response.contentType()).isEqualTo("text/html;charset=utf-8");
            assertThat(response.body()).isEqualTo(expectedBody);
        }
    }

    @Nested
    @DisplayName("등록되지 않은 경로에 대한 요청")
    class UnrecognizedPath {

        @Test
        @DisplayName("기본 응답을 반환한다")
        void unknownPathKeepsDefaultResponse() throws IOException {
            // given
            final var path = "/unknown.html";

            // when
            final var response = resolver.resolve(path);

            // then
            assertThat(response.contentType()).isEqualTo("text/html;charset=utf-8");
            assertThat(response.body()).isEqualTo("Hello world!".getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("상위 디렉터리 접근 시에도 파일을 읽지 않는다")
        void pathsOutsideAllowlistAreNotRead() throws IOException {
            // given
            final var path = "/../login.html";

            // when
            final var response = resolver.resolve(path);

            // then
            assertThat(response.body()).isEqualTo("Hello world!".getBytes(StandardCharsets.UTF_8));
        }
    }
}
