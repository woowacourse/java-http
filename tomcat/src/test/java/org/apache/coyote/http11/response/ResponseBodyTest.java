package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("응답 바디 테스트")
class ResponseBodyTest {

    @DisplayName("바디 길이를 검증한다.")
    @Test
    void contentLength() {
        // given
        String string1 = "abcdef";
        String string2 = "";

        // when
        ResponseBody body1 = new ResponseBody(string1.getBytes());
        ResponseBody body2 = new ResponseBody(string2.getBytes());

        // then
        assertThat(body1.getBodyLength()).isEqualTo(string1.length());
        assertThat(body2.getBodyLength()).isEqualTo(0);
    }
}
