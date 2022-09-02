package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpStatusTest {

    @Test
    void htt_response_status를_반환한다() {
        String expected = "200 OK";
        String actual = HttpStatus.OK.httpResponseHeaderStatus();

        assertThat(actual).isEqualTo(expected);
    }
}
