package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nextstep.jwp.infrastructure.http.request.HttpMethod;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("HTTP Request 파싱 테스트")
    @Test
    void of() {
        final List<String> httpRequest = Arrays.asList(
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*"
        );

        final HttpRequest request = HttpRequest.of(httpRequest);
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpHeaders headers = request.getHeaders();

        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
        assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
        assertThat(headers.getValue("Host")).hasSameElementsAs(Collections.singletonList("localhost:8080"));
        assertThat(headers.getValue("Connection")).hasSameElementsAs(Collections.singletonList("keep-alive"));
        assertThat(headers.getValue("Accept")).hasSameElementsAs(Collections.singletonList("*/*"));
        assertThat(request.getMessageBody()).isEqualTo("");

    }
}