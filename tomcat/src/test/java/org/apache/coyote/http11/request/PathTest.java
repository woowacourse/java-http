package org.apache.coyote.http11.request;

import org.assertj.core.api.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

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

    @Test
    void isSameUri() {
        // given
        final Path path = Path.from("/login");

        // when
        final boolean actual = path.isSameUri("/login");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void isNotSameUri() {
        // given
        final Path path = Path.from("/login");

        // when
        final boolean actual = path.isSameUri("/other");

        // then
        assertThat(actual).isFalse();
    }
}
