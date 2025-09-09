package com.techcourse.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpMethod;
import com.techcourse.http.common.HttpVersion;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("http request 생성")
    @Test
    void ofTest1() {
        // given
        String requestLine = "POST /register HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 25"
        ));
        RequestBody requestBody = RequestBody.from("account=test&password=pass");

        // when
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(httpRequest.getFilePath()).isEqualTo("/register.html"),
                () -> assertThat(httpRequest.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(httpRequest.getContentType()).isEqualTo(ContentType.TEXT_HTML),
                () -> assertThat(httpRequest.getRequestBody()).hasSize(2),
                () -> assertThat(httpRequest.getRequestBody().get("account")).isEqualTo("test"),
                () -> assertThat(httpRequest.getRequestBody().get("password")).isEqualTo("pass")
        );
    }

    @DisplayName("쿼리 파라미터가 있는 요청인 경우")
    @Test
    void ofTest2() {
        // given
        String requestLine = "GET /search?name=java&category=programming HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of("Host: localhost:8080"));
        RequestBody requestBody = RequestBody.empty();

        // when
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // then
        assertAll(
                () -> assertThat(httpRequest.getFilePath()).isEqualTo("/search.html"),
                () -> assertThat(httpRequest.getRequestParams()).hasSize(2),
                () -> assertThat(httpRequest.getRequestParams().get("name")).isEqualTo("java"),
                () -> assertThat(httpRequest.getRequestParams().get("category")).isEqualTo("programming")
        );
    }

    @DisplayName("루트 경로 요청인 경우")
    @Test
    void ofTest3() {
        // given
        String requestLine = "GET / HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of("Host: localhost:8080"));
        RequestBody requestBody = RequestBody.empty();

        // when
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // then
        assertThat(httpRequest.isRootPath()).isTrue();
        assertThat(httpRequest.getFilePath()).isEqualTo("/.html");
    }

    @DisplayName("쿠키가 있는 요청인 경우")
    @Test
    void ofTest4() {
        // given
        String requestLine = "GET /index.html HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Cookie: JSESSIONID=abc123; theme=dark"
        ));
        RequestBody requestBody = RequestBody.empty();

        // when
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // then
        assertThat(httpRequest.getJSessionId()).isEqualTo("abc123");
        assertThat(httpRequest.hasEmptySessionId()).isFalse();
    }

    @DisplayName("잘못된 요청 라인 형식인 경우")
    @Test
    void ofTest5() {
        // given
        String invalidRequestLine = "GET /index.html";
        RequestHeader requestHeader = RequestHeader.from(List.of());
        RequestBody requestBody = RequestBody.empty();

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(invalidRequestLine, requestHeader, requestBody))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("올바르지 않은 요청 형식입니다.");
    }

    @DisplayName("여러 개의 물음표가 있는 url의 경우")
    @Test
    void ofTest6() {
        // given
        String requestLine = "GET /search?name=java?category=programming HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of());
        RequestBody requestBody = RequestBody.empty();

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(requestLine, requestHeader, requestBody))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("잘못된 URI 형식: ?가 여러 번 포함되었습니다 → /search?name=java?category=programming");
    }

    @DisplayName("확장자가 없는 경로에 기본 확장자 추가")
    @Test
    void getFilePathTest1() {
        // given
        String requestLine = "GET /index HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of());
        RequestBody requestBody = RequestBody.empty();

        // when
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // then
        assertThat(httpRequest.getFilePath()).isEqualTo("/index.html");
    }

    @DisplayName("확장자가 있는 경로는 그대로 반환")
    @Test
    void getFilePathTest2() {
        // given
        String requestLine = "GET /styles.css HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of());
        RequestBody requestBody = RequestBody.empty();

        // when
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // then
        assertThat(httpRequest.getFilePath()).isEqualTo("/styles.css");
    }
}
