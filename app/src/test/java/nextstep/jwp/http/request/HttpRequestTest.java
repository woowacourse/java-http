package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("HttpRequest parsing")
    @Test
    void startLineParsingTest() {
        //given

        String httpMessage = "GET /index.html HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*";
        InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //when
        HttpRequest httpRequest = HttpRequest.create(bufferedReader);

        //then
        assertThat(httpRequest.getMethod()).isEqualTo(Method.GET);
        assertThat(httpRequest.getPath()).isEqualTo("/index.html");
        assertThat(httpRequest.getHeader("Host")).contains("localhost:8080");
        assertThat(httpRequest.getHeader("Connection")).contains("keep-alive");
    }
}