package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("Request line을 받으면 Http Request 를 반환한다.")
    void success() {
        String requestLine = "GET /login?key=value HTTP/1.1 ";

        HttpRequest actual = HttpRequest.of(requestLine, HttpHeaders.parse(List.of()), "");

        HttpRequest expected = HttpRequest.of(requestLine, HttpHeaders.parse(List.of()), "");
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("httpMethod 와 경로가 일치하면 True를 반환한다.")
    void matches_success() {
        String requestLine = "GET /login?key=value HTTP/1.1 ";
        HttpRequest httpRequest = HttpRequest.of(requestLine, HttpHeaders.parse(List.of()), "");

        boolean actual = httpRequest.matches("/login", HttpMethod.GET);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Content-Type이 application/x-www-form-urlencoded이면 RequestBody를 QueryParams 형식으로 가져온다.")
    void getFormData_success() {
        String requestLine = "POST /login HTTP/1.1 ";
        HttpRequest httpRequest = HttpRequest.of(requestLine,
            HttpHeaders.parse(List.of("Content-Type: application/x-www-form-urlencoded")),
            "key=value");

        QueryParams actual = httpRequest.getFormData();

        QueryParams expected = QueryParams.from("key=value");
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
