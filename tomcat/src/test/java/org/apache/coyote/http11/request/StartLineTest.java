package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;
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
            softAssertions.assertThat(actual.getPath()).isEqualTo("/index.html");
            softAssertions.assertThat(actual.getQueryString()).isNotPresent();
            softAssertions.assertThat(actual.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        });
    }

    @Test
    void fromWitchQueryString() {
        // given
        final String request = "GET /login?account=gugu&password=password HTTP/1.1";

        // when
        final StartLine actual = StartLine.from(request);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getMethod()).isEqualTo(HttpMethod.GET);
            softAssertions.assertThat(actual.getPath()).isEqualTo("/login");
            softAssertions.assertThat(actual.getQueryString().get().getQueries().get("account")).isEqualTo("gugu");
            softAssertions.assertThat(actual.getQueryString().get().getQueries().get("password")).isEqualTo("password");
            softAssertions.assertThat(actual.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        });
    }
}
