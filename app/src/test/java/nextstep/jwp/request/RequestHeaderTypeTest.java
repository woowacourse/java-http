package nextstep.jwp.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeaderTypeTest {

    @Test
    @DisplayName("Header를 파싱한다.")
    void parseHeaders() {
        final String httpHeaders = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        RequestHeader requestHeader = new RequestHeader(httpHeaders);

        assertThat(requestHeader.contains("Host")).isTrue();
        assertThat(requestHeader.get("Host")).isEqualTo("localhost");
        assertThat(requestHeader.contains("Connection")).isTrue();
        assertThat(requestHeader.get("Connection")).isEqualTo("keep-alive");
        assertThat(requestHeader.contains("localhost")).isFalse();
    }

}