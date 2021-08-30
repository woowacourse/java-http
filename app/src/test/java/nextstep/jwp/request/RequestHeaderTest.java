package nextstep.jwp.request;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestHeaderTest {

    String request = "GET /index HTTP/1.1\r\n" +
            "HOST: localhost:8080\r\n" +
            "Accept-Language: kr";

    @Test
    void of() {
        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String requestLine = bufferedReader.readLine();
            RequestHeader requestHeader = RequestHeader.of(bufferedReader);

            String name1 = "HOST";
            String value1 = "localhost:8080";
            assertThat(requestHeader.getHeader(name1)).isEqualTo(value1);

            String name2 = "Accept-Language";
            String value2 = "kr";
            assertThat(requestHeader.getHeader(name2)).isEqualTo(value2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}