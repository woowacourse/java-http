package org.apache.coyote.request.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class QueryParserTest {

    private static final String QUERY_PARAM = "account=gugu&password=password&email=hkkang@woowahan.com";

    @Test
    @DisplayName("들어온 쿼리를 특정 값을 기준으로 파싱한다.")
    void parse() {
        Map<String, String> params = QueryParser.parse(QUERY_PARAM);

        assertAll(
                () -> assertEquals(params.get("account"), "gugu"),
                () -> assertEquals(params.get("password"), "password"),
                () -> assertEquals(params.get("email"), "hkkang@woowahan.com")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"account=gugu&password=password&em", "acc", "--="})
    @DisplayName("잘못된 형식의 쿼리인 경우 에러를 발생한다.")
    void parse_WhenNotParamForm(String param) {
        assertThrows(IllegalArgumentException.class,
                () -> QueryParser.parse(param));
    }
}
