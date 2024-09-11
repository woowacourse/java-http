package org.apache.coyote.http11.component.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.component.common.body.FormUrlEncodeBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormUrlEncodedBodyTest {

    @Test
    @DisplayName("x-www-form-url-encoded 타입의 Body를 파싱한다.")
    void parse_x_www_form_url_encoded_body() {
        // given
        final var plaintext = "name=fram&password=secret";

        // when
        final var body = new FormUrlEncodeBody(plaintext);

        // then
        assertThat(body.deserialize()).contains("fram");
    }
}
