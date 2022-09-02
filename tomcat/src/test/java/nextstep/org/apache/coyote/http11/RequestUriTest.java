package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.RequestUri;
import org.junit.jupiter.api.Test;

class RequestUriTest {

    @Test
    void hasQueryParamsFalse() {
        // given
        final String uri = "/index.html";

        // when
        final RequestUri requestUri = RequestUri.of(uri);

        // then
        assertThat(requestUri.hasQueryParams()).isFalse();
    }

    @Test
    void hasQueryParamsTrue() {
        // given
        final String uri = "/login?account=gugu&password=password";

        // when
        final RequestUri requestUri = RequestUri.of(uri);

        // then
        assertThat(requestUri.hasQueryParams()).isTrue();
    }

    @Test
    void parseQueryParams() {
        // given
        final String uri = "/login?account=gugu&password=password";

        // when
        final RequestUri requestUri = RequestUri.of(uri);

        // then
        assertAll(() -> {
                    assertThat(requestUri.findQueryParamValue("account")).isEqualTo("gugu");
                    assertThat(requestUri.findQueryParamValue("password")).isEqualTo("password");
                }
        );
    }

    @Test
    void parseResourcePath() {
        // given
        final String uri = "/login?account=gugu&password=password";

        // when
        final RequestUri requestUri = RequestUri.of(uri);

        // then
        assertThat(requestUri.getResourcePath()).isEqualTo("static/login.html");
    }


}
