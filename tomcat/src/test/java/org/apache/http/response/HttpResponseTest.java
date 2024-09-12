package org.apache.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.http.HttpCookie;
import org.apache.http.HttpVersion;
import org.apache.http.header.HttpHeader;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Nested
    @DisplayName("Builder로 HttpResponse 객체 생성")
    class builder {

        @Test
        @DisplayName("Builder로 HttpResponse 객체 생성 성공: 기본값")
        void testBuilderWithDefaultValues() {
            HttpResponse response = HttpResponse.builder().build();

            assertEquals(HttpVersion.HTTP_1_1, response.getVersion());
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals(response.getHeaders(), new HttpHeaders());
            assertEquals("", response.getResponseBody());
        }

        @Test
        @DisplayName("Builder로 HttpResponse 객체 생성 성공: 사용자 정의 값")
        void testBuilderWithCustomValues() {
            HttpResponse response = HttpResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .version(HttpVersion.HTTP_2_0)
                    .addHeader(HttpHeaderName.CONTENT_TYPE, "application/json")
                    .body("{\"message\":\"Not Found\"}")
                    .build();

            assertEquals(HttpVersion.HTTP_2_0, response.getVersion());
            assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
            assertEquals(response.getHeaders(), new HttpHeaders(new HttpHeader(HttpHeaderName.CONTENT_TYPE, "application/json")));
            assertEquals("{\"message\":\"Not Found\"}", response.getResponseBody());
        }
    }

    @Test
    @DisplayName("Cookie 추가")
    void testAddCookie() {
        final HttpCookie httpCookie = HttpCookie.from("sessionId=12345; userId=67890");
        final HttpResponse actual = HttpResponse.builder()
                .addCookie("sessionId", "12345")
                .addCookie("userId", "67890")
                .build();

        assertThat(actual.getHeaders().getSetCookie()).isEqualTo(httpCookie);
    }

    @Test
    @DisplayName("Location 추가")
    void testAddLocation() {
        HttpResponse response = HttpResponse.builder()
                .addLocation("/new-location")
                .build();

        HttpHeader locationHeader = response.getHeaders().getHeader(HttpHeaderName.LOCATION);
        assertNotNull(locationHeader);
        assertEquals("/new-location", locationHeader.getValue());
    }

    @Nested
    @DisplayName("상태 코드에 따른 HttpResponse 객체 생성")
    class build {

        @Test
        @DisplayName("상태 코드에 따른 HttpResponse 객체 생성: OK")
        void testOkBuild() {
            HttpResponse response = HttpResponse.builder().okBuild();
            assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        @DisplayName("상태 코드에 따른 HttpResponse 객체 생성: FOUND")
        void testFoundBuild() {
            HttpResponse response = HttpResponse.builder().foundBuild();
            assertEquals(HttpStatus.FOUND, response.getStatus());
        }

        @Test
        @DisplayName("상태 코드에 따른 HttpResponse 객체 생성: INTERNAL_SERVER_ERROR")
        void testInternalServerErrorBuild() {
            HttpResponse response = HttpResponse.builder().internalServerErrorBuild();
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        }

        @Test
        @DisplayName("상태 코드에 따른 HttpResponse 객체 생성: NOT_FOUND")
        void testNotFoundBuild() {
            HttpResponse response = HttpResponse.builder().notFoundBuild();
            assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        }

        @Test
        @DisplayName("상태 코드에 따른 HttpResponse 객체 생성: UNAUTHORIZED")
        void testUnauthorizedBuild() {
            HttpResponse response = HttpResponse.builder().unauthorizedBuild();
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        }
    }

    @Test
    @DisplayName("지정된 형식으로 문자열 반환 성공")
    void testToString() {
        HttpResponse response = HttpResponse.builder()
                .status(HttpStatus.OK)
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/plain")
                .body("Hello, World!")
                .build();

        String expectedString = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Hello, World!";
        assertEquals(expectedString, response.toString());
    }
}
