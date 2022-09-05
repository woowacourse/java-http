package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("헤더 부분의 입력을 받아서 HttpHeaders를 생성한다.")
    void parseHeaders() {
        // given
        final List<String> headers = new ArrayList<>(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "Accept: */*"
        ));
        final HttpHeaders httpHeaders = new HttpHeaders(headers);

        // when
        final String encodingHeader = httpHeaders.encodingToString();

        // then
        assertThat(encodingHeader).isEqualTo(
                "Host: localhost:8080 " + "\r\n" +
                        "Connection: keep-alive " + "\r\n" +
                        "Accept: */* "
        );
    }
}
