package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.element.Cookie;
import nextstep.jwp.http.message.element.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
