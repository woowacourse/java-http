package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderParserTest {

    @Test
    @DisplayName("문자열 배열의 헤더들을 헤더 객체로 변환한한다.")
    void parse() {
        HttpHeaders headers = HttpHeaderParser.parse(List.of(
                new String[]{"Host:", "localhost:8080"},
                new String[]{"Connection:", "keep-alive"}));

        String host = headers.getValue("Host");
        String connection = headers.getValue("Connection");

        assertThat(host).isEqualTo("localhost:8080");
        assertThat(connection).isEqualTo("keep-alive");
    }
}
