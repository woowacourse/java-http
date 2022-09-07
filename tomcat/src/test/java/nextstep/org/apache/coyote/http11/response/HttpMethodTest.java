package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpMethodTest {

    @DisplayName("http 메서드 맵핑이 잘 되는지 확인한다.")
    @Test
    void of_Post() {
        // given
        final String value = "POST";

        // when
        final HttpMethod httpMethod = HttpMethod.of(value);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.POST);
    }

    @DisplayName("http 메서드 맵핑이 잘 되는지 확인한다.")
    @Test
    void of_Get() {
        // given
        final String value = "GET";

        // when
        final HttpMethod httpMethod = HttpMethod.of(value);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }
}
