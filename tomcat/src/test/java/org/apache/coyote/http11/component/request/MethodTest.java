package org.apache.coyote.http11.component.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.apache.coyote.http11.component.common.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MethodTest {

    @ParameterizedTest
    @DisplayName("메서드 문자에 맞는 메서드 Enum 인스턴스를 반환한다.")
    @EnumSource(value = Method.class)
    void return_method_enum_instance_via_specific_text(final Method expect) {
        // given
        final String text = expect.getValue();

        // when
        final Method actual = Method.from(text);

        // then
        assertThat(expect).isSameAs(actual);
    }

    @Test
    @DisplayName("없는 문자 인스턴화 시도 시 NPE 반환한다.")
    void return_null_when_instantiation_via_invalid_text() {
        // given
        final String text = "geT";

        // when & then
        assertThatThrownBy(() -> Method.from(text))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
