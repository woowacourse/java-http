package org.apache.coyote.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapMakerTest {

    @Test
    @DisplayName("KeyMapper 를 기준으로 Mapping 할 수 있다.")
    void toMap() {

        String[][] input = {
                {"Accept", "*/*"},
                {"Content-Length", "35"},
                {"Content-Type", "text/html"}
        };

        Map<HttpHeaders, String> mappedMap = Stream.of(input)
                .collect(MapMaker.toMap(HttpHeaders::findHeader));

        assertAll(
                () -> assertEquals(3, mappedMap.size()),
                () -> assertEquals("*/*", mappedMap.get(HttpHeaders.ACCEPT)),
                () -> assertEquals("35", mappedMap.get(HttpHeaders.CONTENT_LENGTH)),
                () -> assertEquals("text/html", mappedMap.get(HttpHeaders.CONTENT_TYPE))
        );

    }
}
