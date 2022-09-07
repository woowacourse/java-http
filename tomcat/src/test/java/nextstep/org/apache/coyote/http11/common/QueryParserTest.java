package nextstep.org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.apache.coyote.http11.common.QueryParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParserTest {

    @Test
    @DisplayName("쿼리를 파싱한다.")
    void parse() {
        // given
        String input = "a=b&c=d";

        // when
        Map<String, String> actual = QueryParser.parse(input);

        // then
        Map<String, String> expected = Map.of(
            "a", "b",
            "c", "d"
        );
        assertThat(actual).isEqualTo(expected);
    }
}
