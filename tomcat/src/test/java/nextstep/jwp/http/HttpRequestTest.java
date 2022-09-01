package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @Test
    @DisplayName("Request line을 받으면 Http Request 를 반환한다.")
    void success() {
        String requestLine = "GET /login?key=value HTTP/1.1 ";

        HttpRequest actual = HttpRequest.from(requestLine);

        HttpRequest expected = new HttpRequest("GET", "/login?key=value",
            "/login", new QueryParams(List.of(new QueryParam("key", "value"))));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
