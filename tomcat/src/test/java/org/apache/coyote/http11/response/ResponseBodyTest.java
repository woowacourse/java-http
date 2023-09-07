package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseBodyTest {

    @Test
    void Response_Body의_길이를_잰다() {
        // given
        ResponseBody responseBody = new ResponseBody("Hello World!");

        // when
        int actual = responseBody.measureContentLength();

        // then
        assertThat(actual).isEqualTo(12);
    }
}
