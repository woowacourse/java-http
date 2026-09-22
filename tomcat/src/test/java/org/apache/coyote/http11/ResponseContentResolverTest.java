package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("정적 리소스 응답 선택")
class ResponseContentResolverTest {

    private final ResponseContentResolver resolver = new ResponseContentResolver();

    @Test
    @DisplayName("/login 요청은 HTML 타입을 반환한다")
    void loginPathResolvesToHtmlType() throws IOException {
        // given
        final var path = "/login";

        // when
        final var response = resolver.resolve(path);

        // then
        assertThat(response.contentType()).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    @DisplayName("/login 요청은 로그인 페이지의 내용을 반환한다")
    void loginPathResolvesToLoginPageBody() throws IOException {
        // given
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();

            // when
            final var response = resolver.resolve("/login");

            // then
            assertThat(response.body()).isEqualTo(expectedBody);
        }
    }

    @Test
    @DisplayName("/register 요청은 회원가입 페이지의 내용을 반환한다")
    void registerPathResolvesToRegisterPageBody() throws IOException {
        // given
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/register.html")) {
            assertThat(resource).isNotNull();
            final var expectedBody = resource.readAllBytes();

            // when
            final var response = resolver.resolve("/register");

            // then
            assertThat(response.body()).isEqualTo(expectedBody);
        }
    }

    @Test
    @DisplayName("/401.html 요청은 인증 실패 페이지의 내용을 반환한다")
    void unauthorizedPathResolvesToUnauthorizedPageBody() throws IOException {
        // given
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/401.html")) {
            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();

            // when
            final var response = resolver.resolve("/401.html");

            // then
            assertThat(response.body()).isEqualTo(expectedBody);
        }
    }

    @Nested
    @DisplayName("등록된 정적 리소스를 읽지 못한 경우")
    class ResourceLoadingFailure {

        @Test
        @DisplayName("클래스패스에 파일이 없으면 500 상태의 예외를 던진다")
        void missingResourceBecomesServerError() {
            // given
            final var classLoader = mock(ClassLoader.class);
            when(classLoader.getResourceAsStream("static/index.html")).thenReturn(null);
            final var failingResolver = new ResponseContentResolver(classLoader);

            // when
            final var exception = assertThrows(HttpException.class, () -> failingResolver.resolve("/index.html"));

            // then
            assertThat(exception.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("파일을 읽다 실패하면 원인 예외를 보존한다")
        void readFailurePreservesCause() throws IOException {
            // given
            final var failingStream = mock(InputStream.class);
            when(failingStream.readAllBytes()).thenThrow(new IOException("resource read failed"));
            final var classLoader = mock(ClassLoader.class);
            when(classLoader.getResourceAsStream("static/index.html")).thenReturn(failingStream);
            final var failingResolver = new ResponseContentResolver(classLoader);

            // when
            final var exception = assertThrows(HttpException.class, () -> failingResolver.resolve("/index.html"));

            // then
            assertThat(exception.getCause()).isInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("등록되지 않은 경로에 대한 요청")
    class UnrecognizedPath {

        @Test
        @DisplayName("기본 HTML 타입을 반환한다")
        void unknownPathReturnsDefaultContentType() throws IOException {
            // given
            final var path = "/unknown.html";

            // when
            final var response = resolver.resolve(path);

            // then
            assertThat(response.contentType()).isEqualTo("text/html;charset=utf-8");
        }

        @Test
        @DisplayName("기본 응답 본문을 반환한다")
        void unknownPathReturnsDefaultBody() throws IOException {
            // given
            final var path = "/unknown.html";

            // when
            final var response = resolver.resolve(path);

            // then
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
