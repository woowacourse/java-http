package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.junit.jupiter.api.Test;

class HttpHeaderFactoryTest {

    @Test
    void create() {
        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(HttpHeader.CONTENT_TYPE.getValue(), ContentType.STRINGS.getValue()),
                new Pair(HttpHeader.LOCATION.getValue(), "/index.html")
        );

        assertThat(httpHeaders.getHeaders())
                .containsAllEntriesOf(Map.of(
                                HttpHeader.CONTENT_TYPE.getValue(), ContentType.STRINGS.getValue(),
                                HttpHeader.LOCATION.getValue(), "/index.html"
                        )
                );
    }
}
