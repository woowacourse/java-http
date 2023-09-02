package org.apache.coyote.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class QueryStringsTest {

    @Test
    void 쿼리_스트링을_파싱하고_key_와_value_형태로_저장한다() {
        //given
        final String rawQueryStrings = "name=gugu&password=1234";

        //when
        QueryStrings queryStrings = new QueryStrings(rawQueryStrings);

        //then
        assertAll(
                () -> assertThat(queryStrings.getQueryString("name")).isEqualTo("gugu"),
                () -> assertThat(queryStrings.getQueryString("password")).isEqualTo("1234")
        );
    }
}
