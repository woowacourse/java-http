package org.apache.coyote.http11.request;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class StartLineTest {

    @Test
    void from() {
        // given
        final String request = "GET /index.html HTTP/1.1";

        // when
        final StartLine actual = StartLine.from(request);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getMethod()).isEqualTo(HttpMethod.GET);
            softAssertions.assertThat(actual.getUri()).isEqualTo("/index.html");
            softAssertions.assertThat(actual.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        });
    }
}
