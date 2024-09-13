package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("상태 라인 테스트")
class StatusLineTest {

    @DisplayName("상태 라인 출력에 성공한다.")
    @Test
    void toResponseString() {
        // given
        HttpVersion version = HttpVersion.HTTP_1_1;
        HttpStatusCode code = HttpStatusCode.ACCEPTED;

        // when
        StatusLine statusLine = StatusLine.ofHTTP11(code);

        // then
        assertThat(statusLine.getReponseString()).isEqualTo(version.getVersionString() + " " + code.toStatus() + " ");
    }
}
