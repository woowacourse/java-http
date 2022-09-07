package nextstep.jwp.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusCodeTest {

    @Test
    @DisplayName("Http Response에 들어갈 상태코드를 작성한다.")
    void writeStatus() {
        StatusCode statusCode = StatusCode.NOT_FOUND;

        String actual = statusCode.writeStatus();

        String expected = "404 Not Found";
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
