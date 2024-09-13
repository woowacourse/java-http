package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ProtocolVersionTest {

    @Test
    @DisplayName("모든 프로토콜 버전을 Enum 으로 변환한다.")
    void from() {
        // given
        List<ProtocolVersion> protocolVersions = Arrays.stream(ProtocolVersion.values())
                .toList();

        // when
        List<Executable> executables = protocolVersions.stream()
                .map(version -> (Executable) () -> {
                    String value = version.getValue();
                    ProtocolVersion protocolVersion = ProtocolVersion.from(value);
                    assertThat(protocolVersion).isEqualTo(version);
                })
                .toList();

        // then
        assertAll(executables);
    }

    @Test
    @DisplayName("변환에 실패하면 기본 프로토콜 버전으로 HTTP 1.1 을 반환한다.")
    void fromDefaultIfUnknown() {
        // given
        ProtocolVersion protocolVersion = ProtocolVersion.from("HTTP/4.0");

        // when
        ProtocolVersion expected = ProtocolVersion.HTTP11;

        // then
        assertThat(protocolVersion).isEqualTo(expected);
    }
}
