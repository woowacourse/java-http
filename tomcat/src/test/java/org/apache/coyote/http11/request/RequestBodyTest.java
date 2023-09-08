package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestBodyTest {

    @Test
    void body_문자열을_입력받아_RequestBody를_반환한다() {
        // given
        final String body = "account=gugu&password=password&email=hkkang@woowahan.com";

        // when
        final RequestBody requestBody = RequestBody.from(body);

        // then
        assertThat(requestBody.getItems()).contains(
                entry("account", "gugu"),
                entry("password", "password"),
                entry("email", "hkkang@woowahan.com")
        );
    }

    @Test
    void key를_입력받아_값을_반환한다() {
        // given
        final String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        final RequestBody requestBody = RequestBody.from(body);

        // expect
        assertThat(requestBody.get("account")).isEqualTo("gugu");
    }
}
