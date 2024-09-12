package org.apache.http.response;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StartLineTest {

    @Test
    @DisplayName("지정한 형식으로 문자열 반환 성공")
    void testToString() {
        StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        assertEquals("HTTP/1.1 200 OK\r\n", startLine.toString());
    }

    @Nested
    @DisplayName("문자열로부터 StartLine 객체 생성")
    class from {

        @Test
        @DisplayName("문자열로부터 StartLine 객체 생성 실패: 잘못된 형식")
        void fromWithInvalidFormat() {
            assertThatThrownBy( () -> StartLine.from("InvalidFormat"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("StartLine 형식이 맞지 않습니다. version, status로 구성해주세요.");
        }

        @Test
        @DisplayName("문자열로부터 StartLine 객체 생성 실패: 지원하지 않는 HTTP")
        void fromWithUnsupportedVersion() {
            assertThatThrownBy( () -> StartLine.from("HTTP/11.0 200 OK"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("존재하지 않는 http 프로토콜 버전 : HTTP/11.0입니다.");
        }

        @Test
        @DisplayName("문자열로부터 StartLine 객체 생성 실패: 지원하지 않는 상태 코드")
        void fromWithUnsupportedStatus() {
            assertThatThrownBy( () -> StartLine.from("HTTP/1.1 999 Invalid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("존재하지 않는 상태 코드 : 999입니다.");
        }
    }
}
