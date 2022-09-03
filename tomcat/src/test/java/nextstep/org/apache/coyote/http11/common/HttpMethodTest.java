package nextstep.org.apache.coyote.http11.common;

import org.apache.coyote.common.constant.HttpMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void from() {
        final HttpMethod get = HttpMethod.from("get");

        Assertions.assertThat(get).isEqualTo(HttpMethod.GET);
    }

    @Test
    void invalidMethod() {
        Assertions.assertThatThrownBy(() -> HttpMethod.from("test"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
