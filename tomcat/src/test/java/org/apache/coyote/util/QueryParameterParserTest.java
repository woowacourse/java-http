package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParameterParserTest {


    @DisplayName("하나의 key value로 이뤄진 쿼리파라미터를 파싱한다.")
    @Test
    void parse() {
        String queryParameter = "key1=value1";

        Map<String, List<String>> result = QueryParameterParser.parse(queryParameter);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).containsEntry("key1", List.of("value1"))
        );

    }

    @DisplayName("복수의 쿼리파라미터로 이뤄진 문자열을 파싱한다.")
    @Test
    void parseMultiple() {
        String queryParameter = "key1=value1&key2=value2";

        Map<String, List<String>> result = QueryParameterParser.parse(queryParameter);

        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result).containsEntry("key1", List.of("value1")),
                () -> assertThat(result).containsEntry("key2", List.of("value2"))
        );
    }

    @DisplayName("동일한 키를 가진 쿼리파라미터를 파싱한다.")
    @Test
    void parseDuplicated() {
        String queryParameter = "key1=value1&key1=value2";

        Map<String, List<String>> result = QueryParameterParser.parse(queryParameter);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).containsEntry("key1", List.of("value1", "value2"))
        );
    }

    @DisplayName("빈 문자열을 파싱한다.")
    @Test
    void parseBlank() {
        String queryParameter = "";

        Map<String, List<String>> result = QueryParameterParser.parse(queryParameter);

        assertThat(result).isEmpty();
    }

    @DisplayName("key는 있지만 value가 없는 쿼리파라미터를 파싱한다.")
    @Test
    void parseNoValue() {
        String queryParameter = "key1=";

        Map<String, List<String>> result = QueryParameterParser.parse(queryParameter);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).containsEntry("key1", List.of(""))
        );
    }

    @DisplayName("value가 없는 복수의 쿼리파라미터로 이뤄진 문자열을 파싱한다.")
    @Test
    void parse_ShouldHandleMultipleKeysWithEmptyValues() {
        String queryParameter = "key1=&key2=";

        Map<String, List<String>> result = QueryParameterParser.parse(queryParameter);

        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result).containsEntry("key1", List.of("")),
                () -> assertThat(result).containsEntry("key2", List.of(""))

        );
    }
}
