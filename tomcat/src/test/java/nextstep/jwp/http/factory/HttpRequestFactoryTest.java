package nextstep.jwp.http.factory;

import static nextstep.jwp.http.common.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.Test;

class HttpRequestFactoryTest {

    private static final String VALID_REQUEST = String.join("\r\n",
        "GET /login?account=gugu&password=password HTTP/1.1 ",
        "Host: localhost:8080 ");
    private static final String INVALID_REQUEST = String.join("\r\n",
        "/ GET /login?account=gugu&password=password HTTP/1.1 ",
        "Host: localhost:8080 ",
        "Connection: keep-alive ");

    @Test
    void httpRequest의_Method를_파싱할_수_있다() throws IOException {
        InputStream INPUT_STREAM = new ByteArrayInputStream(VALID_REQUEST.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(INPUT_STREAM));

        HttpRequest httpRequest = HttpRequestFactory.create(bufferedReader);

        String method = httpRequest.getRequestMethod();

        assertThat(method).isEqualTo(GET.getValue());
    }

    @Test
    void httpRequest의_uri를_파싱할_수_있다() throws IOException {
        InputStream INPUT_STREAM = new ByteArrayInputStream(VALID_REQUEST.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(INPUT_STREAM));

        HttpRequest httpRequest = HttpRequestFactory.create(bufferedReader);

        String method = httpRequest.getRequestUri();

        assertThat(method).isEqualTo("/login");
    }

    @Test
    void httpRequest의_queryParameter를_파싱할_수_있다() throws IOException {
        InputStream INPUT_STREAM = new ByteArrayInputStream(VALID_REQUEST.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(INPUT_STREAM));

        HttpRequest httpRequest = HttpRequestFactory.create(bufferedReader);

        Map<String, String> queryParams = httpRequest.getQueryParameters();

        assertThat(queryParams).isEqualTo(
            Map.of("account", "gugu", "password", "password"));
    }
}
