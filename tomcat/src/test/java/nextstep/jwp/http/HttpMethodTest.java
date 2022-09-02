package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotFoundMethodException;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void 없는_http_method를_입력하는_경우_예외가_발생한다() {
        assertThatThrownBy(() -> HttpMethod.from("select"))
                .isInstanceOf(NotFoundMethodException.class);
    }

    @Test
    void http_method로_변환한다() {
        HttpMethod expected = HttpMethod.GET;
        HttpMethod actual = HttpMethod.from("get");

        assertThat(actual).isEqualTo(expected);
    }
}
