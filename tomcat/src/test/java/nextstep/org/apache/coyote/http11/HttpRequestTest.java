package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest는 주어진 Query Param을 파싱한다.")
    void parseQueryParam() {
        // given
        final List<String> rawHttpRequest = new ArrayList<>(
                List.of("GET /login?account=roma&password=password HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "Connection: keep-alive ",
                        "")
        );

        // when
        final HttpRequest request = HttpRequest.from(rawHttpRequest);

        // then
        assertThat(request.getParam("account")).isEqualTo("roma");
        assertThat(request.getParam("password")).isEqualTo("password");
    }
}
