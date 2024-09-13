package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void GET_HTTP_메서드_객체를_생성한다() {
        // given
        String method = "GET";

        // when
        HttpMethod httpMethod = HttpMethod.from(method);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void POST_HTTP_메서드_객체를_생성한다() {
        // given
        String method = "POST";

        // when
        HttpMethod httpMethod = HttpMethod.from(method);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.POST);
    }

    @Test
    void 지원하지_않는_HTTP_메서드일_경우_예외가_발생한다() {
        // given
        String method = "PUT";

        // when & then
        assertThatThrownBy(() -> HttpMethod.from(method))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 HTTP Method 입니다. method: PUT");
    }
}
