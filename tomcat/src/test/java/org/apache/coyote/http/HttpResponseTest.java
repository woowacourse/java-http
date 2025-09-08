package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("기본 HTTP 응답 생성")
    void createBasicHttpResponse() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String body = "Hello World!";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, body);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
        assertThat(responseString).contains("Content-Length: 12");
        assertThat(responseString).contains("Hello World!");
    }

    @Test
    @DisplayName("JSON 응답 생성")
    void createJsonResponse() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String jsonBody = "{\"message\":\"success\"}";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, jsonBody);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Content-Length: 21");
        assertThat(responseString).contains(jsonBody);
    }

    @Test
    @DisplayName("빈 본문으로 HTTP 응답 생성")
    void createResponseWithEmptyBody() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.NO_CONTENT;
        final ContentType contentType = ContentType.HTML;
        final String body = "";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, body);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 204 No Content");
        assertThat(responseString).contains("Content-Length: 0");
        assertThat(responseString).endsWith("\r\n");
    }

    @Test
    @DisplayName("null 본문으로 HTTP 응답 생성")
    void createResponseWithNullBody() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.NO_CONTENT;
        final ContentType contentType = ContentType.HTML;
        final String body = null;

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, body);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 204 No Content");
        assertThat(responseString).contains("Content-Length: 0");
        assertThat(responseString).endsWith("\r\n");
    }

    @Test
    @DisplayName("CSS 파일 응답 생성")
    void createCssResponse() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.CSS;
        final String cssBody = "body { margin: 0; }";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, cssBody);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Content-Type: text/css");
        assertThat(responseString).contains("Content-Length: 19");
        assertThat(responseString).contains(cssBody);
    }

    @Test
    @DisplayName("JavaScript 파일 응답 생성")
    void createJavaScriptResponse() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.JAVASCRIPT;
        final String jsBody = "console.log('Hello');";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, jsBody);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Content-Type: application/javascript");
        assertThat(responseString).contains("Content-Length: 21");
        assertThat(responseString).contains(jsBody);
    }

    @Test
    @DisplayName("에러 응답 생성")
    void createErrorResponse() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.NOT_FOUND;
        final ContentType contentType = ContentType.HTML;
        final String errorBody = "404 - Page Not Found";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, errorBody);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 404 Not Found");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
        assertThat(responseString).contains("Content-Length: 20");
        assertThat(responseString).contains(errorBody);
    }

    @Test
    @DisplayName("서버 에러 응답 생성")
    void createServerErrorResponse() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final ContentType contentType = ContentType.HTML;
        final String errorBody = "Internal Server Error";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, errorBody);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 500 Internal Server Error");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
        assertThat(responseString).contains("Content-Length: 21");
        assertThat(responseString).contains(errorBody);
    }

    @Test
    @DisplayName("다양한 HTTP 버전으로 응답 생성")
    void createResponseWithDifferentHttpVersions() {
        // given
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String body = "Hello";

        // when & then
        final HttpResponse response10 = new HttpResponse("1.0", status, contentType, body);
        assertThat(response10.toString()).contains("HTTP/1.0 200 OK");

        final HttpResponse response11 = new HttpResponse("1.1", status, contentType, body);
        assertThat(response11.toString()).contains("HTTP/1.1 200 OK");
    }

    @Test
    @DisplayName("한글 내용 응답 생성")
    void createResponseWithKoreanContent() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String koreanBody = "안녕하세요!";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, koreanBody);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
        assertThat(responseString).contains("Content-Length: 16"); // UTF-8로 "안녕하세요!" = 16 bytes
        assertThat(responseString).contains(koreanBody);
    }

    @Test
    @DisplayName("CRLF 라인 엔딩 검증")
    void validateCRLFLineEndings() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String body = "Test";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, body);
        final String responseString = response.toString();

        // then
        assertThat(responseString).contains("HTTP/1.1 200 OK\r\n");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8\r\n");
        assertThat(responseString).contains("Content-Length: 4\r\n");
        assertThat(responseString).contains("\r\n\r\nTest"); // 헤더 끝과 본문 구분
    }

    @Test
    @DisplayName("쿠키가 포함된 HTTP 응답 생성")
    void createResponseWithCookies() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String body = "Hello";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, body);
        response.setCookie("JSESSIONID", "ABC123");
        response.setCookie("theme", "dark");

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Set-Cookie: JSESSIONID=ABC123");
        assertThat(responseString).contains("Set-Cookie: theme=dark");
        assertThat(responseString).contains("Content-Length: 5");
        assertThat(responseString).contains(body);
    }

    @Test
    @DisplayName("단일 쿠키가 포함된 HTTP 응답 생성")
    void createResponseWithSingleCookie() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String body = "Welcome";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, body);
        response.setCookie("username", "gugu");

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Set-Cookie: username=gugu");
        assertThat(responseString).contains("Content-Length: 7");
        assertThat(responseString).contains(body);
    }

    @Test
    @DisplayName("쿠키 없는 HTTP 응답 생성")
    void createResponseWithoutCookies() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;
        final ContentType contentType = ContentType.HTML;
        final String body = "No cookies";

        // when
        final HttpResponse response = new HttpResponse(version, status, contentType, body);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).doesNotContain("Set-Cookie:");
        assertThat(responseString).contains("Content-Length: 10");
        assertThat(responseString).contains(body);
    }

    @Test
    @DisplayName("리다이렉트 응답 생성")
    void createRedirectResponse() {
        // given
        final String version = "1.1";
        final String location = "/dashboard";

        // when
        final HttpResponse response = HttpResponse.redirect(version, location);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 302 Found");
        assertThat(responseString).contains("Location: /dashboard");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
        assertThat(responseString).contains("Content-Length: 0");
    }

    @Test
    @DisplayName("절대 경로로 리다이렉트 응답 생성")
    void createRedirectResponseWithAbsolutePath() {
        // given
        final String version = "1.1";
        final String location = "https://www.example.com/login";

        // when
        final HttpResponse response = HttpResponse.redirect(version, location);

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 302 Found");
        assertThat(responseString).contains("Location: https://www.example.com/login");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
        assertThat(responseString).contains("Content-Length: 0");
    }

    @Test
    @DisplayName("쿠키와 함께 리다이렉트 응답 생성")
    void createRedirectResponseWithCookies() {
        // given
        final String version = "1.1";
        final String location = "/index";

        // when
        final HttpResponse response = HttpResponse.redirect(version, location);
        response.setCookie("session", "new_session_id");

        // then
        final String responseString = response.toString();
        assertThat(responseString).contains("HTTP/1.1 302 Found");
        assertThat(responseString).contains("Location: /index");
        assertThat(responseString).contains("Set-Cookie: session=new_session_id");
        assertThat(responseString).contains("Content-Length: 0");
    }
}
