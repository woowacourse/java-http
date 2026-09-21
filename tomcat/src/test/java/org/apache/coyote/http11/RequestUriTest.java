package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUriTest {

    @Test
    void parseLoginQueryString() {
        final var requestUri = RequestUri.from("/login?account=gugu&password=password");

        assertThat(requestUri.path()).isEqualTo("/login");
        assertThat(requestUri.queryParameter("account")).isEqualTo("gugu");
        assertThat(requestUri.queryParameter("password")).isEqualTo("password");
    }

    @Test
    void parseFormParameters() {
        final var parameters = RequestUri.parseParameters(
                "account=gugu&password=password&email=hkkang%40woowahan.com"
        );

        assertThat(parameters)
                .containsEntry("account", "gugu")
                .containsEntry("password", "password")
                .containsEntry("email", "hkkang@woowahan.com");
    }
}
