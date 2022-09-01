package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private static final String VALID_REQUEST = String.join("\r\n",
        "GET /login?account=gugu&password=password HTTP/1.1 ",
        "Host: localhost:8080 ");
    private static final String INVALID_REQUEST = String.join("\r\n",
        "/ GET /login?account=gugu&password=password HTTP/1.1 ",
        "Host: localhost:8080 ");

    @Test
    @DisplayName("HttpRequest의 Method를 파싱할 수 있다.")
    void getMethod() throws IOException {
        InputStream INPUT_STREAM = new ByteArrayInputStream(VALID_REQUEST.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(INPUT_STREAM));

        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        String method = httpRequest.getMethod();

        assertThat(method).isEqualTo("GET");
    }

    @Test
    @DisplayName("HttpRequest의 uri를 파싱할 수 있다.")
    void getUri() throws IOException {
        InputStream INPUT_STREAM = new ByteArrayInputStream(VALID_REQUEST.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(INPUT_STREAM));

        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        String method = httpRequest.getUri().getValue();

        assertThat(method).isEqualTo("/login");
    }

    @Test
    @DisplayName("HttpRequest의 queryParams를 파싱할 수 있다.")
    void getQueryParams() throws IOException {
        InputStream INPUT_STREAM = new ByteArrayInputStream(VALID_REQUEST.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(INPUT_STREAM));

        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        Map<String, String> queryParams = httpRequest.getUri().getQueryParameters();

        assertThat(queryParams).isEqualTo(
            Map.of("account", "gugu", "password", "password"));
    }
}
