package nextstep.jwp.network;

import nextstep.jwp.network.exception.InvalidHttpMethodException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {

    @DisplayName("String으로 HttpMethod를 찾는다 - 성공")
    @Test
    void of() {
        // given
        final String methodName = "GET";

        // when
        final HttpMethod httpMethod = HttpMethod.of(methodName);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("String으로 HttpMethod를 찾는다 - 실패, 유효하지 않은 메서드 이름")
    @Test
    void of_success() {
        // given
        final String methodName = "SLACK";

        // when // then
        assertThatThrownBy(() -> HttpMethod.of(methodName))
                .isInstanceOf(InvalidHttpMethodException.class)
                .hasMessageContaining(methodName);
    }
}