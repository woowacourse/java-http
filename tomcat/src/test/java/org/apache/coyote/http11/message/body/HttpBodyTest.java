package org.apache.coyote.http11.message.body;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @DisplayName("폼 데이터 형식이 아닌 바디 값이 입력되면 예외를 발생시킨다")
    @ValueSource(strings = {"age, 13", "age", "age-13"})
    @ParameterizedTest
    void validateKeyAndValueFormat(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpBody(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 Form Data 값 입니다. - " + input);
    }

    @DisplayName("문자열 키 값이 입력되었을 때 매핑되는 값이 존재하면 반환한다.")
    @Test
    void findValueByKeyWithStringKey() {
        // Given
        final String input = "name=John+Doe&email=john%40example.com";
        final HttpBody httpBody = new HttpBody(input);

        // When
        final Optional<String> name = httpBody.findByKey("name");

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(name).isPresent();
            softAssertions.assertThat(name.get()).isEqualTo("John+Doe");
        });
    }

    @DisplayName("존재하지 않는 키 값이 이력되면 빈 Optional 객체를 반환한다.")
    @Test
    void findValueByKeyWithNotExistValue() {
        // Given
        final HttpBody httpBody = new HttpBody(null);

        // When
        final Optional<String> data = httpBody.findByKey("Kelly");

        // Then
        assertThat(data).isEmpty();
    }
}
