package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.constant.HeaderKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class ResponseHeaderTest {

    @DisplayName("객체 생성 시 빈 헤더 키가 입력된 경우 예외를 반환한다.")
    @Test
    void should_throwException_when_emptyKey() {
        // given & when & then
        assertThatThrownBy(() -> new ResponseHeader(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("헤더의 키는 비어있을 수 없습니다.");
    }

    @DisplayName("객체 생성 시 빈 헤더 값이 입력된 경우 예외를 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void should_throwException_when_invalidValue(String value) {
        // given & when & then
        assertThatThrownBy(() -> new ResponseHeader(HeaderKey.LOCATION, value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("헤더의 값은 비어있을 수 없습니다.");
    }

    @DisplayName("'{키}: {값} '의 형태로 HTTP 응답 메시지를 파싱한다.")
    @Test
    void should_buildHttpMessage() {
        // given
        ResponseHeader header = new ResponseHeader(HeaderKey.LOCATION, "value");

        // when
        String httpMessage = header.buildHttpMessage();

        // then
        assertThat(httpMessage).isEqualTo("Location: value ");
    }
}
