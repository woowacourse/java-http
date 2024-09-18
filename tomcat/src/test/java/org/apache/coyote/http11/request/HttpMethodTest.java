package org.apache.coyote.http11.request;


import org.apache.coyote.http11.request.requestline.HttpMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMethodTest {
    @ParameterizedTest
    @ValueSource(strings = {"repost", "a", "pot"})
    @DisplayName("유효하지 않은 메서드명은 예외가 발생한다")
    void from(String data) {
        Assertions.assertThatThrownBy(() -> HttpMethod.from(data))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pOsT", "dElete", "GEt"})
    @DisplayName("메서드명은 대소문자 구분 없이 찾을 수 있다.")
    void from2(String data) {
        Assertions.assertThatCode(() -> HttpMethod.from(data))
                .doesNotThrowAnyException();
    }
}
