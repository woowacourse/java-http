package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpUriTest {

    @Test
    void equals() {
        //given
        final var uri = "/index.html";
        //when
        final var httpUri = new HttpUri(uri);

        //then
        assertEquals(new HttpUri(uri), httpUri);
    }

}
