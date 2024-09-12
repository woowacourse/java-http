package org.apache.coyote.http11.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.http11.common.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseLineTest {

    @DisplayName("올바른 형식의 응답 라인 문자열을 반환한다.")
    @Test
    void toResponseString() {
        // given
        ResponseLine responseLine = new ResponseLine("HTTP/1.1", HttpStatusCode.OK);

        // when
        String responseString = responseLine.toResponseString();

        // then
        assertEquals("HTTP/1.1 200 OK", responseString);
    }
}
