package org.apache.coyote.http11.request;

import org.assertj.core.api.*;
import org.junit.jupiter.api.Test;

class BodyTest {

    @Test
    void from() {
        // given
        final String request = "account=gugu&password=password";

        // when
        final Body actual = Body.from(request);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getValues().get("account")).isEqualTo("gugu");
            softAssertions.assertThat(actual.getValues().get("password")).isEqualTo("password");
        });
    }
}
