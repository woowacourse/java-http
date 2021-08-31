package nextstep.jwp.web;

import nextstep.jwp.web.exception.NoSuchHttpMethodException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {
    @Test
    @DisplayName("Http 메소드 이름으로 매칭되는 HttpMethod Enum 객체를 찾아올 수 있다.")
    void of() {
        // given
        String methodName = "get";

        // when
        HttpMethod actual = HttpMethod.of(methodName);

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("HTTP 표준 메소드 이외의 메소드가 입력값으로 온 경우 예외를 발생시킨다.")
    public void dummy() {
        // given
        String methodName = "custom";

        // when, then
        assertThatThrownBy(() -> HttpMethod.of(methodName))
                .isInstanceOf(NoSuchHttpMethodException.class);
    }
}
