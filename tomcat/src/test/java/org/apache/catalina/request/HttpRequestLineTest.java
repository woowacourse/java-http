package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;

import org.apache.catalina.http.VersionOfProtocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {
    @Nested
    @DisplayName("생성")
    class Constructor {
        @Test
        @DisplayName("성공 : RequestLine 규정에 따라 적절하게 주어진 경우 생성 성공")
        void ConstructorSuccess() {
            RequestLine requestLine = new RequestLine("GET / HTTP/1.1");

            assertAll(
                    () -> assertThat(requestLine.isSameHttpMethod(HttpMethod.GET)).isTrue(),
                    () -> assertThat(requestLine.getVersionOfProtocol()).isEqualTo(new VersionOfProtocol("HTTP/1.1"))
            );
        }

        @Test
        @DisplayName("실패 : 적절하지 않은 문자열일 경우, 예외 발생")
        void ConstructorFail() {
            String value = "GET / ";

            assertThatThrownBy(() -> new RequestLine(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(value + ": 요청 헤더의 형식이 올바르지 않습니다.");
        }
    }

    @Nested
    @DisplayName("쿼리 파라미터 존재 여부")
    class checkQueryParamIsEmpty {
        @Test
        @DisplayName("성공 : 쿼리 파라미터가 없으면 true 반환")
        void checkQueryParamIsEmptySuccessTrue() {
            RequestLine requestLine = new RequestLine("GET /index HTTP/1.1");

            boolean actual = requestLine.checkQueryParamIsEmpty();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("성공 : 쿼리 파라미터가 있으면 false 반환")
        void checkQueryParamIsEmptySuccessFalse() {
            RequestLine requestLine = new RequestLine("GET /index?account=gugu&password=password HTTP/1.1");

            boolean actual = requestLine.checkQueryParamIsEmpty();

            assertThat(actual).isFalse();
        }
    }

    @Test
    @DisplayName("성공 : Http Method 비교 가능")
    void getHttpMethod() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");

        boolean actual = requestLine.isSameHttpMethod(HttpMethod.GET);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("성공 : query가 포함되지 않은 url 조회 가능")
    void getPathWithoutQuery() {
        RequestLine requestLine = new RequestLine("GET /index?account=gugu&password=password HTTP/1.1");

        String actual = requestLine.getPathWithoutQuery();

        assertThat(actual).isEqualTo("/index");
    }

    @Test
    @DisplayName("성공 : query parameter 조회 가능")
    void getQueryParam() {
        RequestLine requestLine = new RequestLine("GET /index?account=gugu&password=password HTTP/1.1");

        Map<String, String> actual = requestLine.getQueryParam();

        Map<String, String> expected = Map.of("account", "gugu", "password", "password");
        assertThat(actual).isEqualTo(expected);
    }

}
