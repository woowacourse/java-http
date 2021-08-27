package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("request headers")
class RequestHeadersTest {

    @Test
    void BufferedReader를_사용하여_header를_읽어_객체를_생성한다() {
        final String requestAsString = String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "", "requestBody");
        final Map<String, String> expectedHeaders = new HashMap<>() {{
            put("Host", "localhost:8080");
            put("Connection", "keep-alive");
            put("Accept", "*/*");
        }};

        final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final RequestHeaders actual = new RequestHeaders(bufferedReader);
        final Map<String, String> actualHeaders = actual.getHeaders();


        assertThat(actualHeaders).containsAllEntriesOf(expectedHeaders);
    }
}