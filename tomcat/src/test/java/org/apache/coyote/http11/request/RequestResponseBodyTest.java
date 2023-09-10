package org.apache.coyote.http11.request;

import org.assertj.core.api.*;
import org.junit.jupiter.api.Test;

class RequestResponseBodyTest {

    @Test
    void from() {
        // given
        final String request = "account=gugu&password=password";

        // when
        final RequestBody actual = RequestBody.from(request);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getValues().get("account")).isEqualTo("gugu");
            softAssertions.assertThat(actual.getValues().get("password")).isEqualTo("password");
        });
    }
}
