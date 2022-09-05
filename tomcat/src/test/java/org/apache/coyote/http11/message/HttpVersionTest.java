package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.header.HttpVersion;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @Test
    void findByHttpVersionString() {
        // given
        String version = "HTTP/1.1";
        // when
        HttpVersion foundVersion = HttpVersion.findVersion(version);
        // then
        assertThat(foundVersion).isEqualTo(HttpVersion.HTTP11);
    }
}
