package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.header.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MethodTest {

    @Test
    @DisplayName("헤더 첫 줄로 메서드를 파싱할 수 있다")
    void construct() {
        //given
        final String header = "GET / HTTP/1.1";

        //when
        final Method method = Method.from(header);

        //then
        assertThat(method).isEqualTo(Method.GET);
    }

    @Test
    @DisplayName("존재하지 않는 메서드로 파싱하면 예외가 발생한다")
    void construct_notExistMethod() {
        //given
        final String header = "TEST / HTTP/1.1";

        //when, then
        assertThatThrownBy(() -> Method.from(header))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
