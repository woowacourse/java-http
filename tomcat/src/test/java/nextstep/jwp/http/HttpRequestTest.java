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
}
