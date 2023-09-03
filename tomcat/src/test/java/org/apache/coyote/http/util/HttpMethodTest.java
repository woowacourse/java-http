package org.apache.coyote.http.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http.util.exception.UnsupportedHttpMethodException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {

    @Test
    void findMethod_메서드는_지원하는_HTTP_메서드인_경우_해당_HTTP_메서드에_맞는_HttpMethod를_반환한다() {
        final String validMethodName = "get";

        final HttpMethod actual = HttpMethod.findMethod(validMethodName);

        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @Test
    void findMethod_메서드는_지원하지_않는_HTTP_메서드인_경우_예외가_발생한다() {
        final String invalidMethodName = "invalid";

        assertThatThrownBy(() -> HttpMethod.findMethod(invalidMethodName))
                .isInstanceOf(UnsupportedHttpMethodException.class)
                .hasMessageContaining("지원하지 않는 HTTP Method 입니다.");
    }
}
