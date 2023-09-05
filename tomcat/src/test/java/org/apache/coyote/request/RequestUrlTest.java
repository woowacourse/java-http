package org.apache.coyote.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class RequestUrlTest {

    @Test
    void URL에_쿼리파라미터가_있는경우_제대로_파싱이_되는지_확인한다() {
        final String url = "/login?member=kero&password=keroro";
        final RequestUrl requestUrl = RequestUrl.from(url);

        assertThat(requestUrl).hasToString(url);
    }
    @ParameterizedTest
    @ValueSource(strings = {"/login", "/login.html"})
    void URL에_쿼리가_없는경우_제대로_파싱이_되는지_확인한다(final String url) {
        final RequestUrl requestUrl = RequestUrl.from(url);

        assertThat(requestUrl).hasToString(url);
    }
}
