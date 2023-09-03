package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.RequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class HttpMethodTest {

    @Test
    @DisplayName("request method의 이름으로 requestMethod를 찾아서 반환한다.")
    void findByName() {
        //given
        final String method = "GET";

        //when
        final HttpMethod result = HttpMethod.findByName(method);

        //then
        assertThat(result).isEqualTo(GET);
    }

    @Test
    @DisplayName("일치하는 이름이 없는 경우 예외가 발생한다.")
    void findByName_failByNoneExistedName() {
        //given
        final String method = "FAKE";

        //when & then
        assertThatThrownBy(() -> HttpMethod.findByName(method))
                .isInstanceOf(RequestException.class)
                .hasMessage("올바르지 않은 요청메서드 입니다.");
    }

}
