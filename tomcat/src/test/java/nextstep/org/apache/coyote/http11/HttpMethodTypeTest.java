package nextstep.org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.HttpMethodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTypeTest {

    @DisplayName("request에 해당하는 MethodType을 올바르게 가져올 수 있다.")
    @Test
    void from() {
        // given
        final String request = "GET";
        final HttpMethodType expected = HttpMethodType.GET;

        // when
        final HttpMethodType actual = HttpMethodType.from(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("올바른 MethodType이 아니면 예외 처리한다.")
    @Test
    void from_notMethodType() {
        // given
        final String request = "NOT_METHOD_TYPE";

        // when & then
        assertThatThrownBy(() -> HttpMethodType.from(request))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("해당하는 HttpMethod가 존재하지 않습니다.");
    }
}
