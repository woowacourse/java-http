package org.apache.coyote.http11.component.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.component.common.body.FormUrlEncodeBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormUrlEncodeBodyTest {

    @Test
    @DisplayName("x-www-form-url-encoded 바디 순수 문자열 직렬화")
    void deserialize_form_url_encoded_body() {
        // given
        final var plaintext = "hello=world&name=kim";
        final var formUrlEncodeBody = new FormUrlEncodeBody(plaintext);

        // when
        final String deserialize = formUrlEncodeBody.deserialize();

        // then
        assertThat(deserialize).contains("hello=world");
    }
}
