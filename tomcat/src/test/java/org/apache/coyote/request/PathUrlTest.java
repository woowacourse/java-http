package org.apache.coyote.request;

import org.apache.coyote.common.PathUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class PathUrlTest {

    @Test
    void URL에_쿼리파라미터가_있는경우_제대로_파싱이_되는지_확인한다() {
        final String url = "/login?member=kero&password=keroro";
        final PathUrl pathUrl = PathUrl.from(url);

        assertThat(pathUrl).hasToString(url);
    }
    @ParameterizedTest
    @ValueSource(strings = {"/login", "/login.html"})
    void URL에_쿼리가_없는경우_제대로_파싱이_되는지_확인한다(final String url) {
        final PathUrl pathUrl = PathUrl.from(url);

        assertThat(pathUrl).hasToString(url);
    }
}
