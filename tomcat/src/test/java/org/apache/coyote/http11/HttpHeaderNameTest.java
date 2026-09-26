package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpHeaderNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"Content-Length", "content-length", "CONTENT-LENGTH", "cOnTeNt-LeNgTh"})
    void 대소문자와_관계없이_같은_이름으로_정규화한다(final String raw) {
        assertThat(HttpHeaderName.normalize(raw))
                .isEqualTo(HttpHeaderName.CONTENT_LENGTH.getNormalized());
    }

    @Test
    void 터키어_로케일에서도_정규화가_같다() {
        final Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            assertThat(HttpHeaderName.normalize("TRANSFER-ENCODING"))
                    .isEqualTo("transfer-encoding");
        } finally {
            Locale.setDefault(original);
        }
    }
}