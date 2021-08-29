package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProtocolTest {

    @Test
    void value() {
        assertThat(Protocol.LINE_SEPARATOR.value()).isEqualTo("\r\n");
    }
}