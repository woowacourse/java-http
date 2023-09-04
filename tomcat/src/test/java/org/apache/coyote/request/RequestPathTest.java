package org.apache.coyote.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestPathTest {

    @Test
    void 요청_패스를_생성에_성공한다() {
        assertThatCode(() -> new RequestPath("/index.html"))
                .doesNotThrowAnyException();
    }

    @Test
    void 요청_패스를_null로_생성할_경우_예외가_발생한다() {
        assertThatThrownBy(() -> new RequestPath(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
