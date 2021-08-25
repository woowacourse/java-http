package nextstep.jwp.http.header.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.header.element.HttpStatus;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void asString() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");

        HttpResponse httpResponse = HttpResponse.status(HttpStatus.OK, "test.html");

        assertThat(httpResponse.asString())
            .contains("HTTP/1.1 200 OK",
                "Content-Length: 27",
                "Content-Type: text/html",
                "<title>대시보드</title>");
    }
}
