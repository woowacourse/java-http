package org.apache.coyote.http11.request.http;

import org.apache.coyote.http11.request.line.Protocol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProtocolTest {

    @Nested
    class Protocol을_검증한다 {
        @Test
        void 프로토콜이_유효하면_생성한다() {
            // given
            final String protocol = "HTTP/1.1";

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> Protocol.from(protocol)
            );
        }

        @Test
        void 프로토콜이_유효하지_않으면_예외가_발생한다() {
            // given
            final String protocol = "HTT/1.1";

            // when, then
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> Protocol.from(protocol)
            );
        }
    }

}
