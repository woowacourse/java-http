package org.apache.coyote.response;

import static common.ResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

import common.ResponseStatus;
import org.apache.coyote.response.line.ResponseLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({"OK, false", "EMPTY_RESPONSE_STATUS, true"})
    void Response_Status가_비어있는지_확인한다(ResponseStatus responseStatus, boolean expected) {
        // given
        ResponseLine responseLine = new ResponseLine("HTTP/1.1", responseStatus);

        // when
        boolean actual = responseLine.hasEmptyResponseStatus();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
