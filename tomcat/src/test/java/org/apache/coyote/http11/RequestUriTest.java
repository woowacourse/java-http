package org.apache.coyote.http11;

import org.apache.coyote.exception.HttpParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestUriTest {

    @Test
    void pathWithoutQueryString() {
        // when
        final RequestUri uri = new RequestUri("/login");

        // then
        assertThat(uri.getPath()).isEqualTo("/login");
        assertThat(uri.findParameter("account")).isEmpty();
    }

    @Test
    void separatePathAndQueryString() {
        // when
        final RequestUri uri = new RequestUri("/login?account=gugu&password=secret");

        // then
        assertThat(uri.getPath()).isEqualTo("/login");
        assertThat(uri.findParameter("account")).contains("gugu");
        assertThat(uri.findParameter("password")).contains("secret");
    }

    @Test
    void decodePercentEncodedValue() {
        // when
        final RequestUri uri = new RequestUri("/register?email=hkkang%40woowahan.com");

        // then
        assertThat(uri.findParameter("email")).contains("hkkang@woowahan.com");
    }

    @Test
    void keepQuestionMarkInsideQueryString() {
        // when
        final RequestUri uri = new RequestUri("/search?query=what?");

        // then
        assertThat(uri.getPath()).isEqualTo("/search");
        assertThat(uri.findParameter("query")).contains("what?");
    }

    @Test
    void rejectUriNotStartingWithSlash() {
        // expect
        assertThatThrownBy(() -> new RequestUri("login"))
                .isInstanceOf(HttpParseException.class);
    }
}
