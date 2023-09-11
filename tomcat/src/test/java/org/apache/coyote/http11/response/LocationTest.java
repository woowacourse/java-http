package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.header.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LocationTest {

    @Test
    @DisplayName("헤더 첫 줄로 Location 객체를 생성할 수 있다")
    void construct() {
        //given
        final String header = "GET / HTTP/1.1";

        //when, then
        assertDoesNotThrow(() -> Location.from(header));
    }
}
