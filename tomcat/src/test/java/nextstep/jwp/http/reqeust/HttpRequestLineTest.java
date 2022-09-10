package nextstep.jwp.http.reqeust;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.RequestLineFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @DisplayName("null 값으로 RequestLine을 생성하려고 하면 예외를 발생한다.")
    @Test
    void construct_null_exception() {
        //given
        String invalidRequestLine = null;

        //when & then
        assertThatThrownBy(() -> HttpRequestLine.from(invalidRequestLine))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("공백으로 RequestLine을 생성하려고 하면 예외를 발생한다.")
    @Test
    void construct_blank_exception() {
        //given
        String invalidRequestLine = " ";

        //when & then
        assertThatThrownBy(() -> HttpRequestLine.from(invalidRequestLine))
                .isInstanceOf(RequestLineFormatException.class);
    }
}
