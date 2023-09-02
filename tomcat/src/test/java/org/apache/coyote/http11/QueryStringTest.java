package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QueryStringTest {

    @Test
    void uri를_입력받아_QueryString을_생성한다() {
        // given
        final String uri = "/login?account=gugu&password=password";

        // when
        final QueryString queryString = QueryString.from(uri);

        // then
        assertThat(queryString.getItems()).contains(
                entry("account", "gugu"),
                entry("password", "password")
        );
    }
}
