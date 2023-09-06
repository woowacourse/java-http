package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpStatusCodeTest {

    @Test
    void convertHttpStatusMessage_메서드는_해당_상태_코드를_문자열로_변환해_반환한다() {
        final HttpStatusCode statusCode = HttpStatusCode.OK;

        final String actual = statusCode.convertHttpStatusMessage();

        assertThat(actual).isEqualTo("200 OK");
    }
}
