package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParserTest {

    @DisplayName("")
    @Test
    void extractUrl() {
        String uri = "/login?account=rex&password=password";
        String url = RequestParser.extractUrl(uri);

        assertThat(url).isEqualTo("/login");
    }

    @DisplayName("Query String을 올바르게 parsing 한다.")
    @Test
    void parseUri() {
        String uri = "/login?account=rex&password=password";
        Map<String, String> expected = Map.of("account", "rex",
                "password", "password");

        Map<String, String> actual = RequestParser.parseUri(uri);
        assertThat(actual).containsAllEntriesOf(expected);
    }
}
