package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PathTest {

    @Test
    void uri를_반환한다() {
        // given
        final Path path = Path.from("/login?account=gugu&password=password");

        // expect
        assertThat(path.parseUri()).isEqualTo("/login");
    }

    @Test
    void queryString을_반환한다() {
        // given
        final Path path = Path.from("/login?account=gugu&password=password");

        // when
        final QueryString queryString = path.parseQueryString();

        // then
        assertThat(queryString.getItems()).contains(
                entry("account", "gugu"),
                entry("password", "password")
        );
    }
}
