package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestLineTest {
    @Nested
    @DisplayName("생성")
    class Constructor {
        @Test
        @DisplayName("성공 : RequestLine 규정에 따라 적절하게 주어진 경우 생성 성공")
        void ConstructorSuccess() {
            RequestLine requestLine = new RequestLine("GET / HTTP/1.1");

            assertAll(
                    () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                    () -> assertThat(requestLine.getPath()).isEqualTo("/"),
                    () -> assertThat(requestLine.getHttpProtocol()).isEqualTo(HttpProtocol.HTTP),
                    () -> assertThat(requestLine.getHttpVersion()).isEqualTo("1.1")
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
}
