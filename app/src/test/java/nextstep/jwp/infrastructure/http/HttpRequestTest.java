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
            "POST /register HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com"
        );

        final HttpRequest request = HttpRequest.of(httpRequest);
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpHeaders headers = request.getHeaders();

        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(requestLine.getUri().getBaseUri()).isEqualTo("/register");
        assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
        assertThat(headers.getValue("Host")).hasSize(1).hasSameElementsAs(Collections.singletonList("localhost:8080"));
        assertThat(headers.getValue("Connection")).hasSize(1).hasSameElementsAs(Collections.singletonList("keep-alive"));
        assertThat(headers.getValue("Content-Length")).hasSize(1).hasSameElementsAs(Collections.singletonList("80"));
        assertThat(headers.getValue("Content-Type")).hasSize(1).hasSameElementsAs(Collections.singletonList("application/x-www-form-urlencoded"));
        assertThat(headers.getValue("Accept")).hasSize(1).hasSameElementsAs(Collections.singletonList("*/*"));
        assertThat(request.getMessageBody()).isEqualTo("account=gugu&password=password&email=hkkang%40woowahan.com");
    }
}