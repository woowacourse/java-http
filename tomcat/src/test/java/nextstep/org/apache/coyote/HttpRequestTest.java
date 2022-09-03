package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest는 주어진 HttpRequest String을 파싱하고 관리한다.")
    void httpRequest() {
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
        assertAll(
                () -> assertThat(request.getPath()).isEqualTo("/login"),
                () -> assertThat(request.getParam("account")).isEqualTo("roma"),
                () -> assertThat(request.getParam("password")).isEqualTo("password")
        );
    }
}
