package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("headers에 header정보를 넣으면, 정보를 파싱한뒤 보관한다.")
    @Test
    void putTest() {
        HttpHeaders headers = new HttpHeaders(new HashMap<>());

        List<String> headerList = Arrays.asList("Host: localhost:8080", "Connection: keep-alive");
        for (String header : headerList) {
            headers.put(header);
        }

        assertThat(headers.getHeaderByKey("Host")).isEqualTo("Host: localhost:8080");
        assertThat(headers.getHeaderByKey("Connection")).isEqualTo("Connection: keep-alive");
    }

    @DisplayName("모든 헤더 목록을 요청하면, 개행이 포함된 모든 헤더들이 반환된다.")
    @Test
    void allTest() {
        HttpHeaders headers = new HttpHeaders(new LinkedHashMap<>());

        List<String> headerList = Arrays.asList("Host: localhost:8080", "Connection: keep-alive");
        for (String header : headerList) {
            headers.put(header);
        }

        String expectedHeaders = "Host: localhost:8080" + "\r\n" + "Connection: keep-alive" + "\r\n";

        assertThat(headers.getAllHeaders()).isEqualTo(expectedHeaders);
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