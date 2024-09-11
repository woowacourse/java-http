package org.apache.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @Test
    @DisplayName("문자열로부터 value가 일치하는 enum 값 반환 성공")
    void getHttpVersion() {
        assertThat(HttpVersion.getHttpVersion("HTTP/1.1")).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    @DisplayName("문자열로부터 value가 일치하는 enum 값 반환 실패: 존재하지 않는 value인 경우")
    void getHttpVersion_When_NotExists_Value() {
        assertThatThrownBy(() -> HttpVersion.getHttpVersion("HTTP/3.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 http 프로토콜 버전 : HTTP/3.1입니다.");
    }
}
