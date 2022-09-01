package nextstep.jwp.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("httpMessage를 파싱해서 httpRequest를 생성한다.")
    void from() {
        final String firstLine = "GET /register?name=alex&age=7 HTTP/1.1";
        final List<String> headerPart = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*");
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final HttpRequest request = HttpRequest.from(firstLine, headerPart, requestBody);
        final QueryParams queryParams = request.getQueryParams();

        assertAll(
                () -> Assertions.assertThat(request.getMethod()).isSameAs(HttpMethod.GET),
                () -> Assertions.assertThat(request.getUriPath()).isEqualTo("/register"),
                () -> Assertions.assertThat(queryParams.getValue("name").get()).isEqualTo("alex"),
                () -> Assertions.assertThat(queryParams.getValue("age").get()).isEqualTo("7")
        );
    }
}