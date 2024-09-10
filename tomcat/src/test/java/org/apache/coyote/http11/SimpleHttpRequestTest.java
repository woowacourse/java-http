package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequest 객체 테스트")
class SimpleHttpRequestTest {

    @DisplayName("Http Request Message값을 입력하면 해당 값을 가진 HttpRequest인스턴스를 생성한다.")
    @Test
    void createSimpleHttpRequestInstance() {
        // Given
        final String requestMessage = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n";

        // When
        final SimpleHttpRequest httpRequest = new SimpleHttpRequest(requestMessage);

        // Then
        assertSoftly(softAssertions -> {
            assertThat(httpRequest).isNotNull();
            assertThat(httpRequest.getRequestMessage()).isEqualTo(requestMessage);
        });
    }

    @DisplayName("생성자 인수로 null이 입력되면 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenInputNull() {
        // When & Then
        assertThatThrownBy(() -> new SimpleHttpRequest(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Http 요청 메시지로 null을 입력할 수 없습니다.");
    }

    @DisplayName("HttpRequest 메시지에서 Http Method 값을 찾아 반환한다.")
    @Test
    void getHttpMethod() {
        // Given
        final String requestMessage = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n";
        final SimpleHttpRequest httpRequest = new SimpleHttpRequest(requestMessage);

        // When
        final HttpMethod httpMethod = httpRequest.getHttpMethod();

        // Then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("HttpRequest 메시지에서 Request URI 값을 찾아 반환한다.")
    @Test
    void getRequestUri() {
        // Given
        final String requestMessage = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n";
        final SimpleHttpRequest httpRequest = new SimpleHttpRequest(requestMessage);

        // When
        final String requestUri = httpRequest.getRequestUri();

        // Then
        assertThat(requestUri).isEqualTo("/index.html");
    }
}
