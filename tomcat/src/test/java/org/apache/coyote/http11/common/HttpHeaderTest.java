package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.InvalidHttpResponseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @DisplayName("헤더 생성시 데이터 null 하면 예외를 반환한다.")
    @Test
    void generateHeader() {
        assertThatThrownBy(() -> HttpHeader.of(HttpHeaderType.SET_COOKIE, null))
                .isInstanceOf(InvalidHttpResponseException.class);
    }
}
