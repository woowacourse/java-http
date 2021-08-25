package nextstep.jwp.framework.infrastructure.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Protocol 단위 테스트")
class ProtocolTest {

    @DisplayName("findProtocol 메서드는")
    @Nested
    class Describe_findProtocol {

        @DisplayName("유효한 문자열이 주어지면")
        @Nested
        class Context_valid_string_given {

            @DisplayName("해당하는 프로토콜 버전을 찾는다.")
            @Test
            void it_returns_matching_protocol() {
                // given, when
                Protocol protocol = Protocol.findProtocol("HTTP/1.1");

                // then
                assertThat(protocol).isEqualTo(Protocol.HTTP1);
            }
        }

        @DisplayName("유효하지 않은 문자열이 주어지면")
        @Nested
        class Context_invalid_string_given {

            @DisplayName("해당하는 프로토콜 버전을 찾지 못한.")
            @Test
            void it_throws_exception() {
                // given, when, then
                assertThatCode(() -> Protocol.findProtocol("3"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Invalid Protocol Request");

            }
        }
    }
}
