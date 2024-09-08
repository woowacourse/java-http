package org.apache.coyote.http11.domain.protocolVersion;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VersionTest {

    @Test
    @DisplayName("프로토콜의 버전이 Null인 경우 에러가 발생한다.")
    void makeVersion_WhenVersionIsNull() {
        assertThrows(NullPointerException.class, () -> new Version(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.a", "a", "a.3", "a.a"})
    @DisplayName("프로토콜의 버전이 숫자로 이루어 지지 않는 경우 에러가 발생한다.")
    void makeVersion_WhenVersionIsNotNumber(String inputVersion) {
        assertThrows(IllegalArgumentException.class, () -> new Version(inputVersion));
    }
}
