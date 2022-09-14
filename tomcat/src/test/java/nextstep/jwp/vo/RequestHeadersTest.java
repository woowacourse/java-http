package nextstep.jwp.vo;

import nextstep.jwp.vo.RequestHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequestHeadersTest {

    @Test
    void blankLine() {
        // given
        List<String> rawRequest = List.of("POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "");

        // when then
        assertThatThrownBy(() -> RequestHeaders.from(rawRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doubleDelimiter() {
        // given
        List<String> rawRequest = List.of("POST /login HTTP/1.1",
                "Host: localhost: 8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*");

        // when then
        assertThatThrownBy(() -> RequestHeaders.from(rawRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void noDelimiter() {
        // given
        List<String> rawRequest = List.of("POST /login HTTP/1.1",
                "Hostlocalhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*");

        // when then
        assertThatThrownBy(() -> RequestHeaders.from(rawRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
