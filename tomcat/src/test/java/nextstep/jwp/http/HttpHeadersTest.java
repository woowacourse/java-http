package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("헤더의 내용을 추출하여 Map에 저장한 HttpHeaders 객체를 반환한다.")
    void parse() {
        List<String> lines = List.of("Host: localhost:8080");

        HttpHeaders actual = HttpHeaders.parse(lines);

        HttpHeaders expected = new HttpHeaders(Map.of("Host", "localhost:8080"));
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Content-Type이 일치하면 True를 반환한다.")
    void matchesContentType() {
        HttpHeaders httpHeaders = HttpHeaders.parse(List.of("Content-Type: text/html"));

        boolean actual = httpHeaders.matchesContentType("text/html");

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("ContentLength Header 정보를 반환한다.")
    void getContentLength() {
        HttpHeaders httpHeaders = HttpHeaders.parse(List.of("Content-Length: 10"));

        Integer actual = httpHeaders.getContentLength();

        assertThat(actual).isEqualTo(10);
    }

    @Test
    @DisplayName("Cookie Header 정보를 반환한다.")
    void parseCookie() {
        HttpHeaders httpHeaders = HttpHeaders.parse(List.of("COOKIE: key=value"));

        Cookie actual = httpHeaders.parseCookie();

        Cookie expected = Cookie.parse("key=value");
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
