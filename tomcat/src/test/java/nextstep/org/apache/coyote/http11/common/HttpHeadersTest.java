package nextstep.org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.common.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("HttpHeaders는 Http Header를 파싱하여 다루는 역할을 한다.")
    void getHeader() {
        final List<String> raw = List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );

        final HttpHeaders headers = HttpHeaders.from(raw);

        assertAll(
                () -> assertThat(headers.getHeader("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getHeader("Connection")).isEqualTo("keep-alive")
        );
    }

    @Test
    @DisplayName("HttpHeaders는 없는 Header로 조회시에 null을 반환한다.")
    void getHeaderNull() {
        final List<String> raw = List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );

        final HttpHeaders headers = HttpHeaders.from(raw);

        assertThat(headers.getHeader("Cache")).isNull();
    }
}
