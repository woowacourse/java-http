package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.UncheckedHttpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestLineTest {

    @DisplayName("입력값이 비어있으면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "     "})
    void createWithNull(String input) {
        //when //then
        assertThatThrownBy(() -> HttpRequestLine.from(input))
                .isInstanceOf(UncheckedHttpException.class)
                .hasMessageContaining("Http Request Line은 비어있을 수 없습니다.");
    }

    @DisplayName("입력값을 구분자로 분리한 개수가 일치하지 않으면 예외가 발생한다.")
    @Test
    void createWithInvalidInputSplitCount() {
        //given
        String input = "GET /login HTTP/1.1 daon";

        //when //then
        assertThatThrownBy(() -> HttpRequestLine.from(input))
                .isInstanceOf(UncheckedHttpException.class)
                .hasMessageContaining("Http Request Line 형식이 잘못 되었습니다.");
    }
}
