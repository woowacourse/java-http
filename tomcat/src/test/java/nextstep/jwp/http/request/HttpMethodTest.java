package nextstep.jwp.http.request;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.InvalidRequestMethodException;
import nextstep.jwp.http.common.HttpMethod;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void HttpMethod를_반환할_수_있다() {
        // given
        String httpMethod = "GET";

        // when & then
        assertThat(HttpMethod.find(httpMethod)).isInstanceOf(HttpMethod.class);
    }

    @Test
    void 존재하지_않는_HttpMethod_요청시_예외가_발생한다() {
        // given
        String httpMethod = "Not Method";

        // when & then
        assertThatThrownBy(() -> HttpMethod.find(httpMethod))
            .isInstanceOf(InvalidRequestMethodException.class);
    }
}
