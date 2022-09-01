package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private static final String VALID_REQUEST = "GET /login?account=gugu&password=password HTTP/1.1";
    private static final String INVALID_REQUEST = " / GET /login?account=gugu&password=password HTTP/1.1";


    @Test
    @DisplayName("HttpRequest의 Method를 파싱할 수 있다.")
    void getMethod() {
        HttpRequest httpRequest = HttpRequest.from(VALID_REQUEST);

        String method = httpRequest.getMethod();

        assertThat(method).isEqualTo("GET");
    }

    @Test
    @DisplayName("HttpRequest의 uri를 파싱할 수 있다.")
    void getUri() {
        HttpRequest httpRequest = HttpRequest.from(VALID_REQUEST);

        String method = httpRequest.getUri();

        assertThat(method).isEqualTo("/login?account=gugu&password=password");
    }

    @Test
    @DisplayName("HttpRequest의 path를 파싱할 수 있다.")
    void getPath() {
        HttpRequest httpRequest = HttpRequest.from(VALID_REQUEST);

        String method = httpRequest.getPath();

        assertThat(method).isEqualTo("/login");
    }

    @Test
    @DisplayName("HttpRequest의 contentType을 파싱할 수 있다.")
    void getContentType() {
        HttpRequest httpRequest = HttpRequest.from(VALID_REQUEST);

        String method = httpRequest.getContentType();

        assertThat(method).isEqualTo("text/html");
    }

    @Test
    @DisplayName("HttpRequest의 queryParams를 파싱할 수 있다.")
    void getQueryParams() {
        HttpRequest httpRequest = HttpRequest.from(VALID_REQUEST);

        Map<String, String> queryParams = httpRequest.getQueryParams();

        assertThat(queryParams).isEqualTo(
            Map.of("account", "gugu", "password", "password"));
    }

    @Test
    @DisplayName("HttpRequest의 요청 포멧이 잘못되면 예외가 발생한다.")
    void failed_httpRequest_when_wrong_format() {
        assertThatThrownBy(() -> HttpRequest.from(INVALID_REQUEST));
    }
}
