package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void create() throws IOException {
        String requestMessage = "GET /index.html HTTP/1.1" + System.lineSeparator() +
        "Host: localhost:8080" + System.lineSeparator() +
        "Connection: keep-alive" + System.lineSeparator() +
        "Accept: */*";

        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        assertThat(httpRequest.getHttpMethod()).isEqualTo("GET");
        assertThat(httpRequest.getUri()).isEqualTo("/index.html");
        assertThat(httpRequest.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(httpRequest.getHttpHeaders()).hasSize(3);
    }
}