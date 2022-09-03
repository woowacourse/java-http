package nextstep.org.apache.coyote.http11.common.constant;

import org.apache.coyote.common.constant.HttpMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void from() {
        // given
        final HttpMethod get = HttpMethod.from("get");

        // when & then
        Assertions.assertThat(get).isEqualTo(HttpMethod.GET);
    }

    @Test
    void invalidMethod() {
        // when & then
        Assertions.assertThatThrownBy(() -> HttpMethod.from("test"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
