package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpPathTest {

    @Test
    void 성공() {
        // given
        String uri = "/login?account=gugu&password=password";

        // when
        HttpPath httpPath = HttpPath.from(uri);

        // then
        String path = httpPath.getPath();
        QueryString queryString = httpPath.getQueryStrings();

        assertThat(path).isEqualTo("/login");
        assertThat(queryString.getQueries()).containsAllEntriesOf(Map.of(
            "account", List.of("gugu"),
            "password", List.of("password")
        ));
    }

    @Test
    void 같은_키의_여러개의_값() {
        // given
        String uri = "/login?key=foo&key=bar";

        // when
        HttpPath httpPath = HttpPath.from(uri);

        // then
        QueryString queryString = httpPath.getQueryStrings();

        assertThat(queryString.getQueries()).containsAllEntriesOf(Map.of(
            "key", List.of("foo", "bar")
        ));
    }

    @Test
    void queryString이_없으면_empty() {
        // given
        String uri = "/login";

        // when
        HttpPath httpPath = HttpPath.from(uri);

        // then
        String path = httpPath.getPath();
        QueryString queryString = httpPath.getQueryStrings();

        assertThat(path).isEqualTo("/login");
        assertThat(queryString.getQueries()).isEmpty();
    }
}
