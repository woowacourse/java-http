package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("HttpMethod 파싱 테스트")
    @Test
    void from() {
        assertAll(
                () -> assertThat(HttpMethod.from("POST")).isEqualTo(HttpMethod.POST),
                () -> assertThatThrownBy(() -> HttpMethod.from("NOT_EXIST"))
                        .isInstanceOf(UncheckedServletException.class)
                        .hasMessage("올바르지 않은 HTTP Method입니다.")
        );
    }
}
