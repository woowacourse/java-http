package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class URITest {

    @Test
    void getPath() {
        String path = "/login";
        URI uri = new URI(path + "?id=lala&password=1234");

        assertThat(uri.getPath()).isEqualTo(path);
    }
}
