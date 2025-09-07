package org.apache.catalina.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @DisplayName("from 메서드: 같은 키 값의 헤더는 ,로 이어서 반환한다")
    @Test
    void parseTest1() {
        // given
        String requestLines = """
                GET /index.html HTTP/1.1
                Host: example.com
                Accept: text/html
                Accept: application/json
                """;

        final List<String> strings = Arrays.stream(requestLines.split("\n")).map(String::trim).toList();

        // when
        HttpHeader header = HttpHeader.from(strings);

        // then
        assertThat(header.get("Host")).isEqualTo("example.com");
        assertThat(header.get("Accept")).isEqualTo("text/html, application/json");
    }

}
