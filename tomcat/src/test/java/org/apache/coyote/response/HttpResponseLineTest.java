package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseLineTest {

    @Test
    void HTTP_상태를_설정한다() {
        final HttpResponseLine httpResponseLine = new HttpResponseLine(HttpVersion.HTTP_11);

        httpResponseLine.setHttpStatus(HttpStatus.OK);

        assertThat(httpResponseLine.toString()).contains("200 OK");
    }
}
