package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestParamTest {

    @Test
    void RequestParam_생성() {
        // given
        RequestParams requestParams = RequestParams.parse("account=boxster&password=password");

        // when
        String account = requestParams.getParam("account");

        // then
        assertThat(account).isEqualTo("boxster");
    }
}
