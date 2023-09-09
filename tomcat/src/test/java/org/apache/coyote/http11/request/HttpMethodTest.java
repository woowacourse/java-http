package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpMethodTest {

    @Test
    void 이름에_맞는_요청을_찾을_수_있다_GET() {
        // given
        String name = "get";

        // when
        HttpMethod httpMethod = HttpMethod.from(name);

        // then
        Assertions.assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 이름에_맞는_요청을_찾을_수_있다_POST() {
        // given
        String name = "post";

        // when
        HttpMethod httpMethod = HttpMethod.from(name);

        // then
        Assertions.assertThat(httpMethod).isEqualTo(HttpMethod.POST);
    }

    @Test
    void 같은지_검증_가능() {
        assertAll(
                () -> Assertions.assertThat(HttpMethod.POST.is(HttpMethod.POST)).isTrue(),
                () -> Assertions.assertThat(HttpMethod.GET.is(HttpMethod.POST)).isFalse()
        );
    }

}
