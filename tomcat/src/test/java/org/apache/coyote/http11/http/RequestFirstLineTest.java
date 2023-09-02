package org.apache.coyote.http11.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class RequestFirstLineTest {

    @Test
    void requestFirstLine이_method_uri_version_으로_구성되지_않으면_예외발생() {
        String invalidRequestFirstLine = "GET HTTP/1.1 ";

        assertThatThrownBy(() -> RequestFirstLine.from(invalidRequestFirstLine))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
