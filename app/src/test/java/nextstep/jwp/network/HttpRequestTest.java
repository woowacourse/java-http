package nextstep.jwp.network;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class HttpRequestTest {

    @Test
    void create() {
        // given
        final String requestAsString = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* " +
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when // then
        assertThatCode(() -> HttpRequest.of(bufferedReader))
                .doesNotThrowAnyException();
    }
}