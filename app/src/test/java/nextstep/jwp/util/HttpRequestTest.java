package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void readFirstLine() throws IOException {
        //when
        final String target = "GET /index.html HTTP/1.1\n"
            + "Host: localhost:8080\n"
            + "Connection: keep-alive\n"
            + "Accept: */*";
        final String answer = "/index.html";
        final InputStream inputStream = new ByteArrayInputStream(target.getBytes());
        final HttpRequest httpRequest = HttpRequest.readFromInputStream(inputStream);

        //given
        final String requestedURL = httpRequest.getRequestURLWithoutQuery();

        //then
        assertThat(requestedURL).hasToString(answer);
    }

}