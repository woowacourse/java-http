package org.apache.coyote.http.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http.util.exception.UnsupportedHttpVersionException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpVersionTest {

    @Test
    void findVersion_메서드는_지원하는_버전을_전달하면_버전에_맞는_HttpVersion을_반환한다() {
        final String validVersion = "HTTP/1.1";

        final HttpVersion actual = HttpVersion.findVersion(validVersion);

        assertThat(actual).isEqualTo(HttpVersion.HTTP11);
    }

    @Test
    void findVersion_메서드는_지원하지_않는_버전을_전달하면_예외가_발생한다() {
        final String invalidVersion = "HTTP/9.9";

        assertThatThrownBy(() -> HttpVersion.findVersion(invalidVersion))
                .isInstanceOf(UnsupportedHttpVersionException.class)
                .hasMessageContaining("지원하지 않는 HTTP 버전입니다.");
    }
}
