package http.header;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    void 헤더_파싱_테스트() {
        // given
        List<String> headerLines = List.of("Name: value");

        // when
        HttpHeaders httpHeaders = HttpHeaders.parse(headerLines);

        // then
        assertThat(httpHeaders.getAll()).hasSize(1)
                .containsOnly(HttpHeader.parse("Name: value"));
    }

    @Test
    void 헤더_추가_테스트() {
        // given
        HttpHeaders httpHeaders = HttpHeaders.createEmpty();

        // when
        httpHeaders.add("Name", "value");

        // then
        assertThat(httpHeaders.get("Name")).isEqualTo("value");
    }
}
