package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("headers에 header정보를 넣으면, 정보를 파싱한뒤 보관한다.")
    @Test
    void addTest() {
        HttpHeaders headers = new HttpHeaders(new HashMap<>());

        List<String> headerList = Arrays.asList("Host: localhost:8080", "Connection: keep-alive");
        for (String header : headerList) {
            headers.add(header);
        }

        assertThat(headers.getHeaderByKey("Host")).isEqualTo("Host: localhost:8080");
        assertThat(headers.getHeaderByKey("Connection")).isEqualTo("Connection: keep-alive");
    }

    @DisplayName("없는 header정보를 조회하면, 예외가 발생한다.")
    @Test
    void getHeaderTestFail() {
        HttpHeaders headers = new HttpHeaders(new HashMap<>());

        assertThatThrownBy(() -> {
            assertThat(headers.getHeaderByKey("Host")).isEqualTo("Host: localhost:8080");
        }).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}