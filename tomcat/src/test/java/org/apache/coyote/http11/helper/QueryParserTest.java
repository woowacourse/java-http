package org.apache.coyote.http11.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParserTest {

    @DisplayName("입력받은 문자열을 쿼리 스트링의 형태로 파싱하여 Map 형태로 반환한다.")
    @Test
    void should_returnMap_when_parse() {
        // given
        String input = "key1=value1&key2=value2";
        QueryParser queryParser = QueryParser.getInstance();

        // when
        Map<String, String> queries = queryParser.parse(input);

        // then
        assertThat(queries.get("key1")).isEqualTo("value1");
        assertThat(queries.get("key2")).isEqualTo("value2");
    }

    @DisplayName("유효하지 않은 문자열이 입력된 경우 예외가 발생한다.")
    @Test
    void should_throwException_when_parseInvalidString() {
        // given
        String input = "key1: value1";
        QueryParser queryParser = QueryParser.getInstance();

        // when & then
        assertThatThrownBy(() -> queryParser.parse(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 형태의 쿼리스트링입니다.");
    }
}
