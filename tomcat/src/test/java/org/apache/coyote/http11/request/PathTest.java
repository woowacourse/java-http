package org.apache.coyote.http11.request;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    void from() {
        // given
        final String path = "/login?account=gugu&password=password";

        // when
        final Path actual = Path.from(path);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getUri()).isEqualTo("/login");
            softAssertions.assertThat(actual.getQueryString().getQueryValue("account")).isEqualTo("gugu");
            softAssertions.assertThat(actual.getQueryString().getQueryValue("password")).isEqualTo("password");
        });
    }
}
