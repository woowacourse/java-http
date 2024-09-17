package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Optional;

import org.apache.coyote.http11.message.body.HttpBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("HttpRequest 테스트")
class HttpRequestTest {

    @DisplayName("유효한 값을 입력하면 HttpRequest 인스턴스를 생성해 반환한다.")
    @Test
    void createHttpRequestInstance() {
        // Given
        final String input = """
                POST /register HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Content-Length: 80\r
                Content-Type: application/x-www-form-urlencoded\r
                Accept: */*\r
                \r                
                account=gugu&password=password&email=hkkang%40woowahan.com""";

        // When
        final HttpRequest httpRequest = new HttpRequest(input);

        // Then
        assertThat(httpRequest).isNotNull();
    }

    @DisplayName("생성자에 null 혹은 빈 값이 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateHttpRequestMessageIsNullOrBlank(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequest(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Http Request Message는 null 혹은 빈 값이 입력될 수 없습니다. - " + input);
    }

    @DisplayName("HTTP Request Message에 Body 값이 존재하면 HttpBody객체를 Optional에 감싸 반환한다.")
    @Test
    void getBody() {
        // Given
        final String input = """
                POST /register HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Content-Length: 80\r
                Content-Type: application/x-www-form-urlencoded\r
                Accept: */*\r
                \r                
                account=gugu&password=password&email=hkkang%40woowahan.com""";
        final HttpRequest httpRequest = new HttpRequest(input);

        // When
        final Optional<HttpBody> body = httpRequest.getBody();

        // Then
        assertThat(body).isPresent();
    }

    @DisplayName("HTTP Request Message에 Body 값이 존재하지 않으면 빈 Optional 객체를 반환한다.")
    @Test
    void getBodyWhenNoBody() {
        // Given
        final String input = """
                POST /register HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Content-Length: 80\r
                Content-Type: application/x-www-form-urlencoded\r
                Accept: */*\r\n""";
        final HttpRequest httpRequest = new HttpRequest(input);

        // When
        final Optional<HttpBody> body = httpRequest.getBody();

        // Then
        assertThat(body).isEmpty();
    }
}
