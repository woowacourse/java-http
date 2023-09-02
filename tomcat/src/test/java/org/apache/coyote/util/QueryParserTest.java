package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.coyote.common.QueryString;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class QueryParserTest {

    @Test
    void 성공() {
        // given
        String query = "name=glen&password=1234&key=foo&key=bar";

        // when
        QueryString queryString = QueryParser.parse(query);

        // then
        assertThat(queryString.getQueries()).containsExactlyInAnyOrderEntriesOf(Map.of(
            "name", List.of("glen"),
            "password", List.of("1234"),
            "key", List.of("foo", "bar")
        ));
    }

    @Test
    void 빈값이_들어오면_empty() {
        // given
        String query = "";

        // when
        QueryString queryString = QueryParser.parse(query);

        // then
        assertThat(queryString.getQueries()).containsExactlyEntriesOf(Collections.emptyMap());
    }
}
