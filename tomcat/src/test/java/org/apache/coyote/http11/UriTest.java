package org.apache.coyote.http11;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UriTest {

    @Test
    void parsesQueryParametersWithoutChangingPath() {
        Uri uri = Uri.create("/search?q=hello&page=2");

        assertThat(uri.getPath()).isEqualTo("/search");
        assertThat(uri).extracting("queryMap")
                .isEqualTo(Map.of("q", "hello", "page", "2"));
    }

    @Test
    void preservesEmptyQueryValue() {
        Uri uri = Uri.create("/?q=");

        assertThat(uri).extracting("queryMap").isEqualTo(Map.of("q", ""));
    }

    @Test
    void treatsParameterWithoutEqualsAsEmptyValue() {
        Uri uri = Uri.create("/?q");

        assertThat(uri).extracting("queryMap").isEqualTo(Map.of("q", ""));
    }

    @Test
    void preservesEqualsInQueryValue() {
        Uri uri = Uri.create("/?q=a=b");

        assertThat(uri).extracting("queryMap").isEqualTo(Map.of("q", "a=b"));
    }
}
