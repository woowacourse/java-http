package org.apache.coyote.httpresponse.header;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class LocationHeaderTest {

    @Test
    void 올바른_형식의_헤더로_응답한다() {
        // given
        final String path = "/index.html";
        final LocationHeader locationHeader = new LocationHeader(path);

        // when
        final String actual = locationHeader.getKeyAndValue(ResponseHeaderType.LOCATION);
        final String expected = String.format("Location: %s", path);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
