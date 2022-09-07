package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @DisplayName("Http 요청이 잘 바인딩 되는지 확인한다.")
    @Test
    void from() {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ", "");

        final RequestLine requestLine = RequestLine.of(firstLine);

        // when
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHeaders().size()).isEqualTo(2),
                () -> assertThat(httpRequest.getRequestLine()).isEqualTo(requestLine)
        );
    }
}
