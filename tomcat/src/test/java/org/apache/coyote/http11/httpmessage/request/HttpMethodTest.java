package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpMethodTest {

    @Nested
    @DisplayName("이름 조회 테스트")
    class FindByName {

        @ParameterizedTest
        @EnumSource(value = HttpMethod.class)
        @DisplayName("Http 메서드 이름으로 HttpMethod 객체를 조회할 수 있다.")
        void successTest(HttpMethod givenMethod) {
            HttpMethod findMethod = HttpMethod.findByName(givenMethod.name());

            assertThat(findMethod).isEqualTo(givenMethod);
        }

        @Test
        @DisplayName("잘못된 이름으로 HttpMethod 를 조회하면 예외가 발생한다.")
        void wrongNameTest() {
            assertThatThrownBy(() -> HttpMethod.findByName("wrongName"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 Http 메서드 입니다.");
        }

        @Test
        @DisplayName("null 로 HttpMethod 를 조회하면 예외가 발생한다.")
        void nullNameTest() {
            assertThatThrownBy(() -> HttpMethod.findByName(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 Http 메서드 입니다.");
        }
    }
}
