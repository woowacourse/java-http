package nextstep.jwp.http.header.request;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.header.element.Headers;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @Test
    void getHeader() {
        final Headers headers = new Headers();

        headers.putHeader("Host", "localhost:8080");
        headers.putHeader("Connection", "keep-alive");
        headers.putHeader("Accept", "*/*");

        final String actual = headers.getHeader("Host")
            .orElseThrow(IllegalArgumentException::new);

        assertThat(actual).isEqualTo("localhost:8080");
    }
}
