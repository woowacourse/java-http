package nextstep.jwp.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolTest {

    @Test
    void value() {
        assertThat(Protocol.LINE_SEPARATOR.value()).isEqualTo("\r\n");
    }
}
