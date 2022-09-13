package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.Protocol;
import org.junit.jupiter.api.Test;

class ProtocolTest {

    @Test
    void of() {
        // given
        final String versionValue = "HTTP/1.1";

        // when
        final Protocol actual = Protocol.of(versionValue);

        // then
        assertThat(actual).isEqualTo(Protocol.HTTP1_1);
    }
}
