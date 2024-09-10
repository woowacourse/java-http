package org.apache.coyote.http11.component;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.apache.coyote.http11.component.response.ResponseHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {

    @Test
    @DisplayName("응답 해더 메세지 반환한다.")
    void return_response_header_message() {
        // given
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Content-type", "application-json");
        responseHeader.put("Authorization", "xxxxx");
        responseHeader.put("Content-Length", "1000");

        // when
        final var actual = responseHeader.getResponseText();

        // then
        assertThat(actual).contains("Content-type: application-json\r\n");
    }

}
