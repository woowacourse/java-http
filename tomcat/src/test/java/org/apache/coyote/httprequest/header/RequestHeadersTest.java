package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidHeaderException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class RequestHeadersTest {

    @Test
    void 구분자로_구분된_요청_헤더_문자열로_생성한다() {
        // given
        final String delimiter = "\n";
        final String lines = "Content-Length: 19" + delimiter + "Host: localhost:8080" + delimiter;

        // when, then
        assertThatCode(() -> RequestHeaders.from(lines, delimiter))
                .doesNotThrowAnyException();
    }

    @Test
    void 요청_헤더_형식이_잘못된_경우_생성에_실패한다() {
        // given
        final String delimiter = "\n";
        final String invalidLines = "Bebe=Ethan;";

        // when, then
        assertThatThrownBy(() -> RequestHeaders.from(invalidLines, delimiter))
                .isInstanceOf(InvalidHeaderException.class);
    }
}
