package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.StringJoiner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @DisplayName("'{키}: {값} \r\n{키}: {값} \r\n'의 형태로 HTTP 응답 메시지를 파싱한다.")
    @Test
    void should_buildHttpMessage() {
        // given
        ResponseHeaders headers = new ResponseHeaders();
        headers.addLocation("location1");
        headers.addLocation("location2");

        // when
        StringJoiner joiner = new StringJoiner("");
        headers.buildHttpMessage(joiner);

        // then
        String expected = "Location: location1 \r\n" +
                "Location: location2 ";
        assertThat(joiner.toString()).isEqualTo(expected);
    }
}
