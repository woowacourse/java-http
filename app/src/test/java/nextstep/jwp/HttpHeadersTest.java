package nextstep.jwp;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpHeadersTest {
    @DisplayName("HttpHeaders 생성")
    @Test
    void create() throws IOException {
        String headers = "Host: localhost:8080\r\nConnection: keep-alive\r\nContent-Length: 30";
        HttpHeaders httpHeaders = new HttpHeaders(headers);
        assertThat(httpHeaders.get("Content-Length")).isEqualTo("30");
    }
}
