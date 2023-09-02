package nextstep.jwp.protocol.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolTest {

    @Nested
    class Protocol을_검증한다 {
        @Test
        void 프로토콜이_유효하면_생성한다() {
            // given
            final String protocol = "HTTP/1.1";

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> new Protocol(protocol)
            );
        }

        @Test
        void 프로토콜이_유효하지_않으면_예외가_발생한다() {
            // given
            final String protocol = "HTT/1.1";

            // when, then
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new Protocol(protocol)
            );
        }
    }

}
