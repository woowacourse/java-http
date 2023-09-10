package org.apache.coyote.http11.request;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {

    @Test
    void from() {
        // given
        final String request = "Host: localhost:8080";

        // when
        final RequestHeader actual = RequestHeader.from(request);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getName()).isEqualTo("Host");
            softAssertions.assertThat(actual.getValue()).isEqualTo("localhost:8080");
        });
    }
}
