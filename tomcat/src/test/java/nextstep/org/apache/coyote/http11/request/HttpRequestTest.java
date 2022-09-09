package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {


    @DisplayName("startLine으로부터 parsing이 잘 되는지 확인한다.")
    @Test
    void parseHttpRequest() {
        String startLine = "GET / HTTP/1.1";
        Map<String, String> headers = Map.of("Content-Type", "text/html;charset=utf-8 ", "Content-Length", "12 ");
        String requestBody = "request-body";

        HttpRequest httpRequest = HttpRequest.from(startLine, headers, requestBody);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo("GET"),
                () -> assertThat(httpRequest.getUrl()).isEqualTo("/"),
                () -> assertThat(httpRequest.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeaders()).isEqualTo(
                        Map.of("Content-Type", "text/html;charset=utf-8 ", "Content-Length", "12 "))
        );
    }
}
