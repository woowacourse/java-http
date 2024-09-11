package org.apache.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.apache.http.header.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체 생성 성공")
    void from() {
        HttpHeader[] headers = new HttpHeader[]{
                new HttpHeader("Host", "localhost:8080"),
                new HttpHeader("Connection", "keep-alive")
        };

        HttpRequest request = HttpRequest.from("GET /index.html HTTP/1.1", headers, null);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getPath()).isEqualTo("/index.html"),
                () -> assertThat(request.getVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(request.getHeaders()).hasSize(2),
                () -> assertThat(request.getBody()).isNull()

        );
    }

    @Nested
    @DisplayName("form data 형식인 경우 키에 대한 값을 반환")
    class getFormBodyByKey {

        @Test
        @DisplayName("form data 형식인 경우 키에 대한 값을 반환")
        void shouldReturnCorrectValueForValidKey() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1);
            HttpHeader[] headers = new HttpHeader[]{
                    new HttpHeader("Content-Type", "application/x-www-form-urlencoded"),
            };
            String body = "name=John&age=30&city=NewYork&country=&hobby=reading&hobby=gaming";
            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            assertAll(
                    () -> assertEquals("John", httpRequest.getFormBodyByKey("name")),
                    () -> assertEquals("30", httpRequest.getFormBodyByKey("age")),
                    () -> assertEquals("NewYork", httpRequest.getFormBodyByKey("city")),
                    () -> assertEquals("", httpRequest.getFormBodyByKey("country"))
            );
        }

        @Test
        @DisplayName("form data 형식인 경우 키에 대한 값을 반환: 존재하지 않는 키인 경우 null 반환")
        void shouldReturnNullForNonExistentKey() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1);
            HttpHeader[] headers = new HttpHeader[]{
                    new HttpHeader("Content-Type", "application/x-www-form-urlencoded"),
            };
            String body = "name=John";
            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            assertNull(httpRequest.getFormBodyByKey("email"));
        }

        @Test
        @DisplayName("form data 형식인 경우 키에 대한 값을 반환: 중복된 키가 있을 경우 첫 번째 반환")
        void shouldReturnFirstValueForDuplicateKeys() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1);
            HttpHeader[] headers = new HttpHeader[]{
                    new HttpHeader("Content-Type", "application/x-www-form-urlencoded"),
            };
            String body = "hobby=reading&hobby=gaming";
            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            assertEquals("reading", httpRequest.getFormBodyByKey("hobby"));
        }
    }

    @Test
    @DisplayName("isSameMethod 메소드 성공")
    void iSameMethod() {
        // given
        RequestLine requestLine = new RequestLine(HttpMethod.POST, "/login", HttpVersion.HTTP_1_1);
        HttpRequest request = new HttpRequest(requestLine, null, null);

        // then
        assertAll(
                () -> assertThat(request.isSameMethod(HttpMethod.POST)).isTrue(),
                () -> assertThat(request.isSameMethod(HttpMethod.GET)).isFalse()
        );
    }

    @Test
    @DisplayName("getHeader 메소드 실패: 포함되지 않은 헤더")
    void getHeader() {
        // given
        RequestLine requestLine = new RequestLine(HttpMethod.GET, "/hi", HttpVersion.HTTP_1_1);
        HttpHeader[] headers = new HttpHeader[]{
                new HttpHeader("Content-Type", "application/json"),
                new HttpHeader("Authorization", "Bearer token")
        };
        HttpRequest request = new HttpRequest(requestLine, headers, null);

        // when & then
        assertAll(
                () -> assertThat(request.getHeader("Content-Type")).isEqualTo("application/json"),
                () -> assertThat(request.getHeader("Authorization")).isEqualTo("Bearer token"),
                () -> assertThatThrownBy(() -> request.getHeader("Non-Existent"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("존재 하지 않는 Header")
        );
    }

    @Nested
    class parseCookie {

        @Test
        @DisplayName("HttpCookie 파싱 성공")
        void parseCookie() {
            // given
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
            HttpHeader[] headers = new HttpHeader[]{
                    new HttpHeader("Cookie", "sessionId=abc123; userId=john")
            };
            HttpRequest request = new HttpRequest(requestLine, headers, null);

            // when
            HttpCookie cookie = request.getHttpCookie();

            // then
            assertAll(
                    () -> assertThat(cookie).isNotNull(),
                    () -> assertThat(cookie.getValue("sessionId")).isEqualTo("abc123"),
                    () -> assertThat(cookie.getValue("userId")).isEqualTo("john")
            );
        }

        @Test // TOOD: 마저
        @DisplayName("HttpCookie 파싱 성공")
        void parseCookie_WhenNotExists_ReturnNull() {
            // given
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
            HttpHeader[] headers = new HttpHeader[]{
                    new HttpHeader("Cookie", "sessionId=abc123; userId=john")
            };
            HttpRequest request = new HttpRequest(requestLine, headers, null);

            // when
            HttpCookie cookie = request.getHttpCookie();

            // then
            assertAll(
                    () -> assertThat(cookie).isNotNull(),
                    () -> assertThat(cookie.getValue("sessionId")).isEqualTo("abc123"),
                    () -> assertThat(cookie.getValue("userId")).isEqualTo("john")
            );
        }
    }

    @Test
    @DisplayName("Cookie 헤더가 없는 경우 HttpCookie null 반환")
    void testNoCookie() {
        // given
        RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        HttpHeader[] headers = new HttpHeader[]{
                new HttpHeader("Content-Type", "text/html")
        };
        HttpRequest request = new HttpRequest(requestLine, headers, null);

        assertThat(request.getHttpCookie()).isNull();
    }
}

