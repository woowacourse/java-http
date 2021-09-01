package nextstep.jwp.framework.http.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ProtocolVersionTest {

    @DisplayName("http 프로토콜의 DefaultVersion은 HTTP/1.1 이다.")
    @Test
    void protocolDefault() {
        final ProtocolVersion protocolVersion = ProtocolVersion.defaultVersion();
        assertThat(protocolVersion.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @DisplayName("프로토콜이 같다면 동등성을 확보한다.")
    @Test
    void equality() {
        final ProtocolVersion defaultProtocol = ProtocolVersion.defaultVersion();
        final ProtocolVersion protocol = ProtocolVersion.of("HTTP/1.1");
        assertThat(defaultProtocol).isEqualTo(protocol);
    }
}
