package nextstep.jwp.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.response.response_line.ResponseLine;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void asString() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");

        HttpResponse httpResponse = new HttpResponse(
            new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.OK),
            new Headers(headers),
            new Body("test")
        );

        assertThat(httpResponse.asString())
            .isEqualTo("HTTP/1.1 200 Ok\r\n"
                + "Host: localhost:8080\r\n"
                + "\r\n"
                + "test");
    }
}