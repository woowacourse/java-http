package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {

    @Test
    void HTTP_메소드에_맞는_객체를_반환할_수_있다() {
        // given
        String rawHttpMethod = "GET";

        // when
        HttpMethod httpMethod = HttpMethod.from(rawHttpMethod);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }
}
