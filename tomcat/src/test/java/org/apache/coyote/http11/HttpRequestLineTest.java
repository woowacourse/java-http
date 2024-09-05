package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.UncheckedHttpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @DisplayName("입력값이 비어있으면 예외가 발생한다.")
    @Test
    void createWithNull() {
        //given
        String input = null;

        //when //then
        assertThatThrownBy(() -> new HttpRequestLine(input))
                .isInstanceOf(UncheckedHttpException.class)
                .hasMessageContaining("Http Request Line은 비어있을 수 없습니다.");
    }

    @DisplayName("입력값을 구분자로 분리한 개수가 일치하지 않으면 예외가 발생한다.")
    @Test
    void createWithInvalidInputSplitCount() {
        //given
        String input = "GET /login HTTP/1.1 daon";

        //when //then
        assertThatThrownBy(() -> new HttpRequestLine(input))
                .isInstanceOf(UncheckedHttpException.class)
                .hasMessageContaining("Http Request Line 형식이 잘못 되었습니다.");
    }
}
