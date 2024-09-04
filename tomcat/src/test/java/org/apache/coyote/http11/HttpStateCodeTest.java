package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 상태 코드")
class HttpStateCodeTest {

    @DisplayName("HTTP 상태 코드 응답에 맞게 반환한다.")
    @Test
    void toStatus() {
        // given
        HttpStatusCode stateCode = HttpStatusCode.OK;
        String expected = stateCode.getCode() + " " + stateCode.getMessage();

        // when & then
        assertThat(stateCode.toStatus()).isEqualTo(expected);
    }
}
