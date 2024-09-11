package org.apache.catalina.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.catalina.http.HttpProtocol;
import org.apache.catalina.http.VersionOfProtocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class VersionOfProtocolTest {
    @Nested
    @DisplayName("생성")
    class Constructor {
        @Test
        @DisplayName("성공 : /로 이루어진 문자열일 경우, 생성 성공")
        void ConstructorSuccess() {
            VersionOfProtocol versionOfProtocol = new VersionOfProtocol("HTTP/1.1");

            assertAll(
                    () -> assertThat(versionOfProtocol.getHttpProtocol()).isEqualTo(HttpProtocol.HTTP),
                    () -> assertThat(versionOfProtocol.getHttpVersion()).isEqualTo("1.1")
            );
        }

        @Test
        @DisplayName("실패 : /로 이루어지지 않은 문자열일 경우, 예외 발생")
        void ConstructorFail() {
            String value = "HTTP?1.1";

            assertThatThrownBy(() -> new VersionOfProtocol(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(value + ": HTTP 프로토콜 및 버전 형식이 잘못되었습니다.");
        }
    }
}
