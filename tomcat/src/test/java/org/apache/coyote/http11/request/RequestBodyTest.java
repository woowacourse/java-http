package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestBodyTest {

    @Test
    void RequestBody_생성() {
        // given
        RequestBody requestBody = RequestBody.parse("account=boxster&password=password");

        // when
        String account = requestBody.getBody("account");

        // then
        assertThat(account).isEqualTo("boxster");
    }
}
