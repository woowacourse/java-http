package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.StatusCodeNotExistsException;
import org.junit.jupiter.api.Test;

class HttpStatusTest {

    @Test
    void 코드로부터_HttpStatus를_찾는다() {
        // when
        int code = 200;
        String reasonPhrase = "OK";
        HttpStatus httpStatus = HttpStatus.from(code);

        // then
        assertAll(
                () -> assertThat(httpStatus.getCode()).isEqualTo(code),
                () -> assertThat(httpStatus.getReasonPhrase()).isEqualTo(reasonPhrase)
        );
    }

    @Test
    void 존재하지_않는_코드로_조회하면_예외가_발생한다() {
        // given
        int invalidCode = 999;

        // when & then
        assertThatThrownBy(() -> HttpStatus.from(invalidCode))
                .isInstanceOf(StatusCodeNotExistsException.class);
    }
}
