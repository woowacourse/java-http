package org.apache.coyote.http11.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormUrlEncodedBodyTest {

    @Test
    @DisplayName("x-www-form-url-encoded 타입의 Body를 파싱한다.")
    void parse_x_www_form_url_encoded_body() {
        // given
        final var plaintext = "name=fram&password=secret";
        final var body = new FormUrlEncodedBody();

        // when
        body.consume(plaintext);

        // then
        assertThat(body.get("name")).isEqualTo("fram");
    }


    @Test
    @DisplayName("x-www-form-url-encoded 타입의 매개변수의 키-값의 각각 1개가 아니라면 예외를 발생한다.")
    void throw_exception_when_invalid_param_key_and_value_count() {
        // given
        final var plaintext = "name=fram&password=";
        final var body = new FormUrlEncodedBody();

        // when & then
        assertThatThrownBy(() -> body.consume(plaintext))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
