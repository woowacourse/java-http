package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestBodyTest {

    @Test
    void 생성자는_바디를_전달하면_HttpRequestBody를_초기화한다() {
        final HttpRequestBody actual = new HttpRequestBody("Hello World");

        assertThat(actual).isNotNull();
    }

    @Test
    void isEmpty_메서드는_바디가_비어_있으면_true를_반환한다() {
        final HttpRequestBody body = HttpRequestBody.EMPTY;

        final boolean actual = body.isEmpty();

        assertThat(actual).isTrue();
    }

    @Test
    void isEmpty_메서드는_바디가_비어_있지_않으면_false를_반환한다() {
        final HttpRequestBody body = new HttpRequestBody("Hello World!");

        final boolean actual = body.isEmpty();

        assertThat(actual).isFalse();
    }

    @Test
    void body_메서드는_body를_반환한다() {
        final String bodyContent = "Hello World!";
        final HttpRequestBody body = new HttpRequestBody(bodyContent);

        final String actual = body.body();

        assertThat(actual).isEqualTo(bodyContent);
    }
}
