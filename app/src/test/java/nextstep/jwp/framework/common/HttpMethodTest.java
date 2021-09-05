package nextstep.jwp.framework.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {

    @DisplayName("HttpMethod 를 valueOf 메서드로 생성한다.")
    @Test
    void create() {
        HttpMethod httpMethod = HttpMethod.valueOf("GET");
        assertThat(httpMethod).isSameAs(HttpMethod.GET);
    }

    @DisplayName("올바르지 않은 값으로 HttpMethod 생성한다.")
    @Test
    void createWithInvalidValue() {
        assertThatThrownBy(() -> HttpMethod.valueOf("ABC"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
