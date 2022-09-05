package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("지원하지 않는 Http Method로 생성하면 예외를 반환한다.")
    void of() {
        assertThatThrownBy(() -> HttpMethod.of("invalid"))
                .isExactlyInstanceOf(MethodNotAllowedException.class);
    }
}
