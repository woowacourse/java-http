package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.http.message.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("String으로 된 Http Method를 Enum타입의 Http Method로 반환한다.")
    @Test
    void of_변환_성공() {
        // given
        String method = "get";

        // when
        HttpMethod actual = HttpMethod.of(method);

        // then
        assertThat(actual).isSameAs(HttpMethod.GET);
    }

    @DisplayName("존재하지 않는 Http Method타입으로 변환시 예외가 발생한다.")
    @Test
    void of_변환_실패() {
        // given
        String method = "gget";

        // when
        assertThatThrownBy(() -> HttpMethod.of(method))
            .isInstanceOf(RuntimeException.class);
    }
}
