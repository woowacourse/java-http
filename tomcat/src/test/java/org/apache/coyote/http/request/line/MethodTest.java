package org.apache.coyote.http.request.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MethodTest {

    @Test
    @DisplayName("GET을 전달하면 GET Enum을 반환한다.")
    void fromGetMethod() {
        // given
        Method method = Method.from("GET");

        // when & then
        assertThat(method).isEqualTo(Method.GET);
    }

    @Test
    @DisplayName("POST를 전달하면 POST Enum을 반환한다.")
    void fromPostMethod() {
        // given
        Method method = Method.from("POST");

        // when & then
        assertThat(method).isEqualTo(Method.POST);
    }

    @Test
    @DisplayName("지원하지 않는 메서드를 전달하면 UncheckedServletException 예외가 발생한다.")
    void unsupportedMethodThrowsException() {
        // given
        String unsupportedMethod = "INVALID";

        // when & then
        assertThatThrownBy(() -> Method.from(unsupportedMethod))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessageContaining("지원하지 않는 HTTP 메서드입니다.");
    }
}
