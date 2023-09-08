package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.line.ResponseLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseLineTest {

    @Test
    void ResponseLine을_문자열_메시지로_만든다() {
        // given
        ResponseLine responseLine = new ResponseLine("HTTP/1.1", OK);

        // when
        String message = responseLine.responseLineMessage();

        // then
        assertThat(message).isEqualTo("HTTP/1.1 200 OK");
    }
}
