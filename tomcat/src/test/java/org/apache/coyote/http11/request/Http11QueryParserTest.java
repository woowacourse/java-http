package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11QueryParserTest {

    @Test
    @DisplayName("한개의 쿼리 스트링이 포함된 경우 잘 파싱하는지 확인")
    void parseSingle() {
        Http11QueryParser queryStringParser = new Http11QueryParser();

        var result = queryStringParser.parse("/login?account=1");

        assertThat(result).containsExactly(new Http11Query("account", "1"));
    }

    @Test
    @DisplayName("여러개의 쿼리 스트링이 포함된 경우 잘 파싱하는지 확인")
    void parseMany() {
        Http11QueryParser queryStringParser = new Http11QueryParser();

        var result = queryStringParser.parse("/login?account=1&password=1234");

        assertThat(result).containsExactly(new Http11Query("account", "1"), new Http11Query("password", "1234"));
    }

    @Test
    @DisplayName("쿼리 스트링이 없는 경우 빈 Map을 반환하는지 확인")
    void parseNone() {
        Http11QueryParser queryStringParser = new Http11QueryParser();

        var result = queryStringParser.parse("/login");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("쿼리 스트링이 잘못된 형식으로 있는 경우 가능한 부분만 파싱하는지 확인")
    void parseWeired() {
        Http11QueryParser queryStringParser = new Http11QueryParser();

        var result = queryStringParser.parse("/login?account=&password=1");

        assertThat(result).containsExactly(new Http11Query("password", "1"));
    }
}
