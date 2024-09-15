package org.apache.tomcat.util.http.parser;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryStringParserTest {

    @DisplayName("Query String 형식의 문자열을 파싱할 수 있다.")
    @Test
    void parse() {
        Map<String, String> actual = QueryStringParser.parse("account=gugu&password=password&email=hkkang%40woowahan.com");
        Map<String, String> expected = Map.of("account", "gugu", "password", "password", "email", "hkkang@woowahan.com");
        assertThat(actual).isEqualTo(expected);
    }
}
