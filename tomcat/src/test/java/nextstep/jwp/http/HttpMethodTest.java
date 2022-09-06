package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("Enum의 이름과 일치하면 해당 Enum을 가져온다.")
    void from() {
        HttpMethod actual = HttpMethod.from("GET");

        HttpMethod expected = HttpMethod.GET;
        assertThat(actual).isEqualTo(expected);
    }
}
