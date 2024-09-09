package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestParserTest {

    private final Http11RequestParser requestParser = new Http11RequestParser();

    @Test
    @DisplayName("InputStream에서 문자열 데이터를 잘 읽는지 확인")
    void readAsString() {
        String expected = UUID.randomUUID().toString();
        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());

        String read = requestParser.readAsString(inputStream);

        assertThat(read).isEqualTo(expected);
    }

    @Test
    @DisplayName("body를 잘 파싱하는지 확인")
    void parseBody() {
        String requestMessage = """
                POST /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                \r
                first=1&second=2""";

        LinkedHashMap<String, String> body = requestParser.parseBody(requestMessage);

        assertThat(body)
                .containsExactly(entry("first", "1"), entry("second", "2"));
    }

    @Test
    @DisplayName("body가 비어있는 경우 빈 배열을 반환하는지 확인")
    void parseBodyWhenEmpty() {
        String requestMessage = """
                POST /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r""";

        LinkedHashMap<String, String> body = requestParser.parseBody(requestMessage);

        assertThat(body).isEmpty();
    }
}
