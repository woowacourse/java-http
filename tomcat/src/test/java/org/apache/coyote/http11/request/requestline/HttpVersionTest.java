package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.HttpVersionNotSupportedException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HttpVersionTest {
    @ParameterizedTest
    @ValueSource(strings = {"http/1.1", "HTTP/1", "HTTP/1.1.1", "HTTPS/1.1", "HTTP/1.x", ""})
    void 형식이_틀린_버전은_400(final String raw) {
        assertThatThrownBy(() -> HttpVersion.from(raw))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"HTTP/2.0", "HTTP/3.0", "HTTP/0.9"})
    void 형식은_맞지만_미지원_버전은_505(final String raw) {
        assertThatThrownBy(() -> HttpVersion.from(raw))
                .isInstanceOf(HttpVersionNotSupportedException.class);
    }
}