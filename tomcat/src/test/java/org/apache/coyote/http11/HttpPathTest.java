package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpPathTest {

    @Test
    void equals() {
        //given
        final var uri = "/index.html";
        //when
        final var httpUri = new HttpPath(uri);

        //then
        assertEquals(new HttpPath(uri), httpUri);
    }

}
