package org.apache.coyote.http11.message.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpHeaderTest {

    @DisplayName("유효한 값이 입력되면 인스턴스를 반환한다.")
    @Test
    void createHttpHeaderInstance() {
        // Given
        final String input = """
                Host: localhost:8080
                Connection: keep-alive
                Accept: */*
                Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46""";

        // When
        final HttpHeader httpHeader = new HttpHeader(input);

        // Then
        assertThat(httpHeader).isNotNull();
    }

    @DisplayName("생성자에 Null 혹은 빈 값이 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateHttpHeaderValueIsNullOrBlank(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpHeader(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("HTTP Request Header 값은 Null 혹은 빈 값이 올 수 없습니다. - " + input);
    }

    @DisplayName("{key: value} 형식이 아닌 헤더 값이 입력되면 예외를 발생시킨다")
    @ValueSource(strings = {"host, localhost", "host", "host-localhost"})
    @ParameterizedTest
    void validateFieldKeyAndValueFormat(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpHeader(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 Header 필드 값 입니다. - " + input);
    }

    @DisplayName("HttpHeaderFieldType 객체 키 값이 입력되었을 때 매핑되는 값이 존재하면 반환한다.")
    @Test
    void findValueByKeyWithHttpHeaderFieldTypeKey() {
        // Given
        final String input = """
                Host: localhost:8080
                Connection: keep-alive
                Accept: */*
                Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46""";
        final HttpHeader httpHeader = new HttpHeader(input);

        // When
        final Optional<String> host = httpHeader.findValueByKey(HttpHeaderFieldType.HOST);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(host).isPresent();
            softAssertions.assertThat(host.get()).isEqualTo("localhost:8080");
        });
    }

    @DisplayName("문자열 키 값이 입력되었을 때 매핑되는 값이 존재하면 반환한다.")
    @Test
    void findValueByKeyWithStringKey() {
        // Given
        final String input = """
                Host: localhost:8080
                Connection: keep-alive
                Accept: */*
                Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46""";
        final HttpHeader httpHeader = new HttpHeader(input);

        // When
        final Optional<String> host = httpHeader.findValueByKey("Host");

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(host).isPresent();
            softAssertions.assertThat(host.get()).isEqualTo("localhost:8080");
        });
    }

    @DisplayName("존재하지 않는 키 값이 이력되면 빈 Optional 객체를 반환한다.")
    @Test
    void findValueByKeyWithNotExistValue() {
        // Given
        final String input = """
                Host: localhost:8080
                Connection: keep-alive
                Accept: */*
                Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46""";
        final HttpHeader httpHeader = new HttpHeader(input);

        // When
        final Optional<String> field = httpHeader.findValueByKey("Kelly");

        // Then
        assertThat(field).isEmpty();
    }
}
