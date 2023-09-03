package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QueryStringParserTest {

    @Test
    void 쿼리_스트링을_파싱한다() {
        // given
        String queryString = "account=testuser&password=password123";

        // when
        QueryStringParser parser = QueryStringParser.from(queryString);

        // then
        assertThat(parser.getParamAndValues())
                .extracting("account", "password")
                .containsExactly("testuser", "password123");
    }
}
