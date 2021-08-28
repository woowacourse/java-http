package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("HTTP Request가 오면, 데이터를 파싱해서 보관한다.")
    @Test
    void httpRequestTest() throws IOException {
        final String text = String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            ""
        );

        final InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequest httpRequest = new HttpRequest(bufferedReader);
        inputStream.close();

        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
    }
}