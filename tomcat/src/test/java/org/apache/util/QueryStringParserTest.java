package org.apache.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringParserTest {

    @DisplayName("쿼리 문자를 파싱한다.")
    @Test
    void parseQueryString() {
        Map<String, List<String>> queryString = QueryStringParser.parseQueryString("account=gugu&password=password");

        assertThat(queryString)
                .containsExactlyInAnyOrderEntriesOf(Map.of("account", List.of("gugu"), "password", List.of("password")));
    }

    @DisplayName("쿼리 문자 형식에 맞는지 확인한다. - True")
    @Test
    void validateQueryStringTrue() {
        String value = "name=John&age=30&city=NewYork";

        boolean isQueryString = QueryStringParser.isQueryString(value);

        assertThat(isQueryString).isTrue();
    }

    @DisplayName("쿼리 문자 형식에 맞는지 확인한다. - True")
    @Test
    void validateQueryStringFalse() {
        String value = "name=John&&age=30&city=NewYork";

        boolean isQueryString = QueryStringParser.isQueryString(value);

        assertThat(isQueryString).isFalse();
    }
}
