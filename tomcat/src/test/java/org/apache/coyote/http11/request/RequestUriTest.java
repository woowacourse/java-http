package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestUriTest {

    @Test
    void RequestUri로_파라미터를_반환한다() {
        // given
        RequestUri requestUri = RequestUri.of("/login?account=boxster&password=password");

        // when
        String account = requestUri.getRequestParam("account");
        String password = requestUri.getRequestParam("password");

        // then
        assertThat(account).isEqualTo("boxster");
        assertThat(password).isEqualTo("password");
    }

    @Test
    void RequestUri로_파라미터가_없는_경우_빈값을_반환한다() {
        // given
        RequestUri requestUri = RequestUri.of("/login");

        // when
        String account = requestUri.getRequestParam("account");
        String password = requestUri.getRequestParam("password");

        // then
        assertThat(account).isEmpty();
        assertThat(password).isEmpty();
    }

    @Test
    void 요청_uri를_반환한다() {
        // given
        RequestUri requestUri = RequestUri.of("/login?account=boxster&password=password");

        // when
        String uri = requestUri.getUri();

        // then
        assertThat(uri).isEqualTo("/login");
    }

}
