package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("상태 라인 테스트")
class StatusLineTest {

    @DisplayName("상태 라인 출력에 성공한다.")
    @Test
    void toResponseString() {
        // given
        String version = "HTTP/1.1";
        HttpStatusCode code = HttpStatusCode.ACCEPTED;

        // when
        StatusLine statusLine = new StatusLine(version, code);

        // then
        assertThat(statusLine.getReponseString()).isEqualTo(version + " " + code.toStatus() + " ");
    }
}
