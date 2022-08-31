package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.junit.jupiter.api.Test;

class HttpHeaderFactoryTest {

    @Test
    void create() {
        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(HttpHeader.CONTENT_TYPE, ContentType.STRINGS.getValue()),
                new Pair(HttpHeader.LOCATION, "/index.html")
        );

        assertThat(httpHeaders.getHeaders())
                .containsAllEntriesOf(Map.of(
                                HttpHeader.CONTENT_TYPE, ContentType.STRINGS.getValue(),
                                HttpHeader.LOCATION, "/index.html"
                        )
                );
    }
}
