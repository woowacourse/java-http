package org.apache.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.common.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {

    @Test
    void 일치하는_Http_Method를_반환한다() {
        String method = "GET";

        HttpMethod httpMethod = HttpMethod.of(method);

        assertThat(httpMethod.getName()).isEqualTo(method);
    }

    @Test
    void 일치하는_Http_Method가_없으면_예외가_발생한다() {
        String method = "AA";

        assertThatThrownBy(() -> HttpMethod.of(method))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("허용되지 않는 HttpMethod 입니다.");

    }
}
