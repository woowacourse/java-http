package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;
import org.assertj.core.api.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RequestLineTest {

    @Test
    void from() {
        // given
        final String request = "GET /index.html HTTP/1.1";

        // when
        final RequestLine actual = RequestLine.from(request);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getMethod()).isEqualTo(Method.GET);
            softAssertions.assertThat(actual.getUri()).isEqualTo("/index.html");
            softAssertions.assertThat(actual.getQueryString()).isNotPresent();
            softAssertions.assertThat(actual.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        });
    }

    @Test
    void fromWitchQueryString() {
        // given
        final String request = "GET /login?account=gugu&password=password HTTP/1.1";

        // when
        final RequestLine actual = RequestLine.from(request);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getMethod()).isEqualTo(Method.GET);
            softAssertions.assertThat(actual.getUri()).isEqualTo("/login");
            softAssertions.assertThat(actual.getQueryString().get().getQueries().get("account")).isEqualTo("gugu");
            softAssertions.assertThat(actual.getQueryString().get().getQueries().get("password")).isEqualTo("password");
            softAssertions.assertThat(actual.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        });
    }

    @Test
    void isSameMethod() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");

        // when
        final boolean actual = requestLine.isSameMethod(Method.GET);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void isNotSameMethod() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");

        // when
        final boolean actual = requestLine.isSameMethod(Method.POST);

        // then
        assertThat(actual).isFalse();
    }
}
