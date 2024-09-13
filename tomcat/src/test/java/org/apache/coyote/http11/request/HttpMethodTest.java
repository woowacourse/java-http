package org.apache.coyote.http11.request;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMethodTest {
    @DisplayName("유효하지 않은 메서드명은 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"repost", "a", "pot"})
    void from(String data) {
        Assertions.assertThatThrownBy(() -> HttpMethod.from(data))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메서드명은 대소문자 구분 없이 찾을 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"pOsT", "dElete", "GEt"})
    void from2(String data) {
        Assertions.assertThatCode(() -> HttpMethod.from(data))
                .doesNotThrowAnyException();
    }
}
