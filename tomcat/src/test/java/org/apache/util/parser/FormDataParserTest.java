package org.apache.util.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormDataParserTest {

    private final FormDataParser parser = new FormDataParser();

    @DisplayName("parse 메서드는 폼 데이터를 올바르게 파싱한다.")
    @Test
    void parse() {
        // given
        String body = "username=johndoe&password=secure123";

        // when
        Map<String, String> result = parser.parse(body);

        // then
        assertAll("FormDataParser should parse form data correctly",
                () -> assertThat(result).containsEntry("username", "johndoe"),
                () -> assertThat(result).containsEntry("password", "secure123")
        );
    }

    @DisplayName("parse 메서드는 빈 문자열을 빈 맵으로 반환한다.")
    @Test
    void parseEmptyString() {
        // given
        String body = "";

        // when
        Map<String, String> result = parser.parse(body);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("parse 메서드는 잘못된 형식의 데이터를 처리한다.")
    @Test
    void parseInvalidFormat() {
        // given
        String body = "keyWithoutValue&key2=value2";

        // when
        Map<String, String> result = parser.parse(body);

        // then
        assertAll(
                () -> assertThat(result).containsEntry("key2", "value2"),
                () -> assertThat(result).doesNotContainKey("")
        );
    }
}
