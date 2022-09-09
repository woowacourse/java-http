package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("startLine으로부터 parsing이 잘 되는지 확인한다.")
    @Test
    void parseHttpRequest() {
        String startLine = "GET / HTTP/1.1";

        RequestLine httpRequest = RequestLine.from(startLine);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getPath()).isEqualTo("/")
        );
    }
}
