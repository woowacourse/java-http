package nextstep.jwp.web.network.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThatCode;

class HttpHeadersTest {

    @DisplayName("HttpHeaders 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final String headersAsString = String.join("\r\n",
                "Host: localhost:8080 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 1000 ",
                "");
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(headersAsString.getBytes())));

        // when // then
        assertThatCode(() -> HttpHeaders.of(bufferedReader))
                .doesNotThrowAnyException();
    }
}