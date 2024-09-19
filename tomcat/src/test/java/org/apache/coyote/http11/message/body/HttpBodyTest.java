package org.apache.coyote.http11.message.body;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpBodyTest {

    @DisplayName("유효한 값이 입력되면 인스턴스를 반환한다.")
    @Test
    void createHttpBodyInstance() {
        // Given
        final String input = "name=John+Doe&email=john%40example.com";

        // When
        final HttpBody httpbody = new HttpBody(input);

        // Then
        assertThat(httpbody).isNotNull();
    }


    @DisplayName("바디 값이 존재한다면 값을 담은 Optional 객체를 반환한다.")
    @Test
    void getValueWhenValueIsExist() {
        // Given
        final String input = "name=John+Doe&email=john%40example.com";
        final HttpBody httpBody = new HttpBody(input);

        // When
        final Optional<String> value = httpBody.getValue();

        // Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(value).isPresent();
            softAssertions.assertThat(value.get()).isEqualTo("name=John+Doe&email=john%40example.com");
        });
    }

    @DisplayName("바디 값이 존재하지 않다면 빈 Optional 객체를 반환한다.")
    @Test
    void getValueWhenValueIsNotExist() {
        // Given
        final HttpBody httpBody = new HttpBody(null);

        // When
        final Optional<String> value = httpBody.getValue();

        // Then
        assertThat(value).isEmpty();
    }

    @DisplayName("키와 값으로 구성된 바디 값이라면 Map 형태로 키와 값을 반환한다.")
    @Test
    void parseFormDataKeyAndValue() {
        // Given
        final String input = "name=John+Doe&email=john%40example.com";
        final HttpBody httpBody = new HttpBody(input);

        // When
        final Map<String, String> keyAndValue = httpBody.parseFormDataKeyAndValue();

        // Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(keyAndValue).isNotEmpty();
            softAssertions.assertThat(keyAndValue.get("name")).isEqualTo("John+Doe");
            softAssertions.assertThat(keyAndValue.get("email")).isEqualTo("john%40example.com");
        });
    }

    @DisplayName("키와 값으로 구성되지 않은 바디 값이라면 빈 Map 객체를 반환한다.")
    @Test
    void parseFormDataKeyAndValueWithNotKeyAndValueFormatData() {
        // Given
        final String input = "Hello Kelly!";
        final HttpBody httpBody = new HttpBody(input);

        // When
        final Map<String, String> keyAndValue = httpBody.parseFormDataKeyAndValue();

        // Then
        assertThat(keyAndValue).isEmpty();
    }
}
