package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import common.ContentType;
import common.HttpStatus;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpResponseTest {

    static Stream<Arguments> emptyBodies() {
        return Stream.of(Arguments.of(""), Arguments.of((String) null));
    }

    static Stream<Arguments> okResponses() {
        return Stream.of(
                Arguments.of(ContentType.TEXT_HTML, "Hello World!",
                        "Hello World!".getBytes(StandardCharsets.UTF_8).length),
                Arguments.of(ContentType.TEXT_HTML, "{\"message\":\"success\"}",
                        "{\"message\":\"success\"}".getBytes(StandardCharsets.UTF_8).length),
                Arguments.of(ContentType.TEXT_CSS, "body { margin: 0; }",
                        "body { margin: 0; }".getBytes(StandardCharsets.UTF_8).length),
                Arguments.of(ContentType.APPLICATION_JAVASCRIPT, "console.log('Hello');",
                        "console.log('Hello');".getBytes(StandardCharsets.UTF_8).length)
        );
    }

    static Stream<Arguments> errorResponses() {
        return Stream.of(
                Arguments.of(HttpStatus.NOT_FOUND, "404 - Page Not Found", "HTTP/1.1 404 Not Found",
                        "404 - Page Not Found".getBytes(StandardCharsets.UTF_8).length),
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                        "HTTP/1.1 500 Internal Server Error",
                        "Internal Server Error".getBytes(StandardCharsets.UTF_8).length)
        );
    }

    static Stream<Arguments> invalidCookieCases() {
        return Stream.of(
                Arguments.of("session\r\nSet-Cookie: admin=true", "value", "쿠키 이름에 유효하지 않은 문자가 포함되어 있습니다"),
                Arguments.of("session\u0000", "value", "쿠키 이름에 유효하지 않은 문자가 포함되어 있습니다"),
                Arguments.of(null, "value", "쿠키 이름은 null이거나 비어있을 수 없습니다"),
                Arguments.of("", "value", "쿠키 이름은 null이거나 비어있을 수 없습니다"),
                Arguments.of("session", "abc123\r\nSet-Cookie: admin=true", "쿠키 값에 유효하지 않은 문자가 포함되어 있습니다"),
                Arguments.of("session", "value;path=/", "쿠키 값에 유효하지 않은 문자가 포함되어 있습니다"),
                Arguments.of("session", null, "쿠키 값은 null일 수 없습니다")
        );
    }

    @ParameterizedTest(name = "{0} 응답, 길이={2}")
    @MethodSource("okResponses")
    @DisplayName("OK 응답 - 다양한 본문/타입")
    void createOkResponses(ContentType contentType, String body, int expectedLength) {
        final HttpStatusLine statusLine = HttpStatusLine.from("1.1", HttpStatus.OK);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 200 OK");
            softly.assertThat(responseString).contains(contentType == ContentType.TEXT_CSS ? "Content-Type: text/css" :
                    contentType == ContentType.APPLICATION_JAVASCRIPT ? "Content-Type: application/javascript" :
                            "Content-Type: text/html;charset=UTF-8");
            softly.assertThat(responseString).contains("Content-Length: " + expectedLength);
            if (body != null) {
                softly.assertThat(responseString).contains(body);
            }
        });
    }

    @ParameterizedTest(name = "204 No Content 바디: {0}")
    @MethodSource("emptyBodies")
    @DisplayName("빈/null 본문으로 HTTP 응답 생성")
    void createResponseWithEmptyOrNullBody(String body) {
        final HttpStatusLine statusLine = HttpStatusLine.from("1.1", HttpStatus.NO_CONTENT);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);
        final HttpResponseBody responseBody = HttpResponseBody.from(
                body == null ? null : body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 204 No Content");
            softly.assertThat(responseString).contains("Content-Length: 0");
            softly.assertThat(responseString).endsWith("\r\n");
        });
    }

    @ParameterizedTest(name = "에러 응답: {0}")
    @MethodSource("errorResponses")
    @DisplayName("에러 응답 생성")
    void createErrorResponses(HttpStatus status, String body, String expectedStatusLine, int expectedLength) {
        final HttpStatusLine statusLine = HttpStatusLine.from("1.1", status);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);
        final HttpResponseBody responseBody = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains(expectedStatusLine);
            softly.assertThat(responseString).contains("Content-Type: text/html;charset=UTF-8");
            softly.assertThat(responseString).contains("Content-Length: " + expectedLength);
            softly.assertThat(responseString).contains(body);
        });
    }

    @Test
    @DisplayName("다양한 HTTP 버전으로 응답 생성")
    void createResponseWithDifferentHttpVersions() {
        // given
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.TEXT_HTML;
        final String body = "Hello";

        // when & then
        final HttpStatusLine statusLine10 = HttpStatusLine.from("1.0", status);
        final HttpResponseHeader header10 = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody10 = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response10 = HttpResponse.from(statusLine10, header10, responseBody10);

        final HttpStatusLine statusLine11 = HttpStatusLine.from("1.1", status);
        final HttpResponseHeader header11 = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody11 = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response11 = HttpResponse.from(statusLine11, header11, responseBody11);
        assertSoftly(softly -> {
            softly.assertThat(response10.toString()).contains("HTTP/1.0 200 OK");
            softly.assertThat(response11.toString()).contains("HTTP/1.1 200 OK");
        });
    }

    @Test
    @DisplayName("한글 내용 응답 생성")
    void createResponseWithKoreanContent() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.TEXT_HTML;
        final String koreanBody = "안녕하세요!";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody = HttpResponseBody.from(koreanBody.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        // then
        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 200 OK");
            softly.assertThat(responseString).contains("Content-Type: text/html;charset=UTF-8");
            softly.assertThat(responseString).contains("Content-Length: 16"); // UTF-8로 "안녕하세요!" = 16 bytes
            softly.assertThat(responseString).contains(koreanBody);
        });
    }

    @Test
    @DisplayName("CRLF 라인 엔딩 검증")
    void validateCRLFLineEndings() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.TEXT_HTML;
        final String body = "Test";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);
        final String responseString = response.toString();

        // then
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 200 OK\r\n");
            softly.assertThat(responseString).contains("Content-Type: text/html;charset=UTF-8\r\n");
            softly.assertThat(responseString).contains("Content-Length: 4\r\n");
            softly.assertThat(responseString).contains("\r\n\r\nTest"); // 헤더 끝과 본문 구분
        });
    }

    @Test
    @DisplayName("쿠키가 포함된 HTTP 응답 생성")
    void createResponseWithCookies() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.TEXT_HTML;
        final String body = "Hello";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);
        response.setCookie("JSESSIONID", "ABC123");
        response.setCookie("theme", "dark");

        // then
        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 200 OK");
            softly.assertThat(responseString).contains("Set-Cookie: theme=dark");
            softly.assertThat(responseString).contains("Content-Length: 5");
            softly.assertThat(responseString).contains(body);
        });
    }

    @Test
    @DisplayName("단일 쿠키가 포함된 HTTP 응답 생성")
    void createResponseWithSingleCookie() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.TEXT_HTML;
        final String body = "Welcome";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);
        response.setCookie("username", "gugu");

        // then
        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 200 OK");
            softly.assertThat(responseString).contains("Set-Cookie: username=gugu");
            softly.assertThat(responseString).contains("Content-Length: 7");
            softly.assertThat(responseString).contains(body);
        });
    }

    @Test
    @DisplayName("쿠키 없는 HTTP 응답 생성")
    void createResponseWithoutCookies() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.TEXT_HTML;
        final String body = "No cookies";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);
        final HttpResponseBody responseBody = HttpResponseBody.from(body.getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        // then
        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 200 OK");
            softly.assertThat(responseString).doesNotContain("Set-Cookie:");
            softly.assertThat(responseString).contains("Content-Length: 10");
            softly.assertThat(responseString).contains(body);
        });
    }

    @Test
    @DisplayName("리다이렉트 응답 생성")
    void createRedirectResponse() {
        // given
        final String version = "1.1";
        final String location = "/dashboard";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, HttpStatus.FOUND);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);
        header.add("Location", location);
        final HttpResponseBody responseBody = HttpResponseBody.empty();
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        // then
        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 302 Found");
            softly.assertThat(responseString).contains("Location: /dashboard");
            softly.assertThat(responseString).contains("Content-Length: 0");
        });
    }

    @Test
    @DisplayName("절대 경로로 리다이렉트 응답 생성")
    void createRedirectResponseWithAbsolutePath() {
        // given
        final String version = "1.1";
        final String location = "https://www.example.com/login";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, HttpStatus.FOUND);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);
        header.add("Location", location);
        final HttpResponseBody responseBody = HttpResponseBody.empty();
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        // then
        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 302 Found");
            softly.assertThat(responseString).contains("Location: https://www.example.com/login");
            softly.assertThat(responseString).contains("Content-Length: 0");
        });
    }

    @Test
    @DisplayName("쿠키와 함께 리다이렉트 응답 생성")
    void createRedirectResponseWithCookies() {
        // given
        final String version = "1.1";
        final String location = "/index";

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, HttpStatus.FOUND);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);
        header.add("Location", location);
        final HttpResponseBody responseBody = HttpResponseBody.empty();
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);
        response.setCookie("session", "new_session_id");

        // then
        final String responseString = response.toString();
        assertSoftly(softly -> {
            softly.assertThat(responseString).contains("HTTP/1.1 302 Found");
            softly.assertThat(responseString).contains("Location: /index");
            softly.assertThat(responseString).contains("Set-Cookie: session=new_session_id");
            softly.assertThat(responseString).contains("Content-Length: 0");
        });
    }

    @ParameterizedTest(name = "쿠키 유효성 실패 #{index}")
    @MethodSource("invalidCookieCases")
    @DisplayName("쿠키 유효성 검사 예외")
    void cookieValidationErrors(final String name, final String value, final String expectedMessageContains) {
        final HttpStatusLine statusLine = HttpStatusLine.from("1.1", HttpStatus.OK);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);
        final HttpResponseBody responseBody = HttpResponseBody.from("test".getBytes(StandardCharsets.UTF_8));
        final HttpResponse response = HttpResponse.from(statusLine, header, responseBody);

        assertThatThrownBy(() -> response.setCookie(name, value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessageContains);
    }
}
