package nextstep.jwp.httpmessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpStatusCode 테스트")
class HttpStatusCodeTest {

    @Test
    void findBy() {
        //given
        final int statusCode = 200;
        //when
        final HttpStatusCode actual = HttpStatusCode.findBy(statusCode);
        //then
        assertThat(actual.getValue()).isEqualTo(statusCode);
    }
}