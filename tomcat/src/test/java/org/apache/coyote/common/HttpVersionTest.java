package org.apache.coyote.common;

import org.apache.coyote.httprequest.exception.InvalidHttpVersionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class HttpVersionTest {

    @Test
    void 잘못된_형식의_HTTP_프로토콜으로는_생성에_실패한다() {
        // given
        final String invalidHttpProtocol = "BEBE/1.1";

        // when, then
        assertThatThrownBy(() -> HttpVersion.from(invalidHttpProtocol))
                .isInstanceOf(InvalidHttpVersionException.class);
    }

    @Test
    void 잘못된_형식의_HTTP_버전으로는_생성에_실패한다() {
        // given
        final String invalidHttpVersion = "HTTP/1-1";

        // when, then
        assertThatThrownBy(() -> HttpVersion.from(invalidHttpVersion))
                .isInstanceOf(InvalidHttpVersionException.class);
    }
}
