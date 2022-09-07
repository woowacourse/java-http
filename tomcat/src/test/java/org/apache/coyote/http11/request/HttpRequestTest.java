package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
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
                "Content-Type: application/json"
        );
        final HttpRequest request = HttpRequest.from(firstLine, headerPart, "");
        final QueryParams queryParams = request.getQueryParams();
        final HttpHeaders headers = request.getHeaders();
        assertAll(
                () -> assertThat(request.getMethod()).isSameAs(HttpMethod.GET),
                () -> assertThat(request.getUriPath()).isEqualTo("/register"),
                () -> assertThat(queryParams.getValue("name").get()).isEqualTo("alex"),
                () -> assertThat(queryParams.getValue("age").get()).isEqualTo("7"),
                () -> assertThat(headers.getValue("Host").get()).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getValue("Connection").get()).isEqualTo("keep-alive"),
                () -> assertThat(headers.getValue("Content-Type").get()).isEqualTo("application/json")
        );
    }

    @Test
    @DisplayName("쿼리파라미터가 없는 HttpRequest를 생성한다.")
    void fromWithoutQueryString() {
        final String firstLine = "GET /register HTTP/1.1";
        final List<String> headerPart = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/json"
        );
        final HttpRequest request = HttpRequest.from(firstLine, headerPart, "");
        final QueryParams queryParams = request.getQueryParams();
        final HttpHeaders headers = request.getHeaders();
        assertAll(
                () -> assertThat(request.getMethod()).isSameAs(HttpMethod.GET),
                () -> assertThat(request.getUriPath()).isEqualTo("/register"),
                () -> assertThat(queryParams.getValue("name")).isEmpty(),
                () -> assertThat(queryParams.getValue("age")).isEmpty(),
                () -> assertThat(headers.getValue("Host").get()).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getValue("Connection").get()).isEqualTo("keep-alive"),
                () -> assertThat(headers.getValue("Content-Type").get()).isEqualTo("application/json")
        );
    }
}
