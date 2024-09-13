package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseStatusTest {

    @DisplayName("'{상태코드} {상태메시지}'의 형태로 HTTP 응답 메시지를 파싱한다.")
    @Test
    void should_buildHttpMessage() {
        // given
        ResponseStatus status = ResponseStatus.OK;

        // when
        String httpMessage = status.buildHttpMessage();

        // then
        assertThat(httpMessage).isEqualTo("200 OK");
    }
}
