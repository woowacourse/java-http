package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.line.RequestUri;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestUriTest {

    @Test
    void Request_uri의_구성을_확인한다() {
        // given
        String requestUri = "/index.html";
        RequestUri uri = RequestUri.from(requestUri);

        // when
        boolean actual = uri.consistsOf("/index.html");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Request_uri에_Query_String이_있는지_확인한다() {
        // given
        String requestUri = "/login?account=gugu&password=password";
        RequestUri uri = RequestUri.from(requestUri);

        // when
        boolean actual = uri.hasQueryString();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Request_uri의_Query_String의_값을_꺼낸다() {
        // given
        String requestUri = "/login?account=gugu&password=password";
        RequestUri uri = RequestUri.from(requestUri);

        // when
        String account = uri.getQueryStringValue("account");

        // then
        assertThat(account).isEqualTo("gugu");
    }
}
