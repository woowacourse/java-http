package org.apache.coyote.http11.request.requestLine;

import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.request.requestLine.requestUri.RequestUri;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

    @Test
    @DisplayName("문자열로 주어진 request line을 HTTP 메서드, uri, http 버전으로 구분한다.")
    void parseRequestLine() {
        // given
        final String givenHttpMethod = "GET";
        final String givenRequestUri = "/members?name=encho&age=23";
        final String givenHttpVersion = "HTTP/1.1";
        final String givenRequestLine = String.join(" ", givenHttpMethod, givenRequestUri, givenHttpVersion);

        final HttpMethod expectHttpMethod = HttpMethod.from(givenHttpMethod);
        final RequestUri expectRequestUri = RequestUri.from(givenRequestUri);
        final HttpVersion expectHttpVersion = HttpVersion.from(givenHttpVersion);

        // when
        final RequestLine actual = RequestLine.from(givenRequestLine);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getHttpMethod()).isEqualTo(expectHttpMethod);
            softAssertions.assertThat(actual.getRequestUri()).isEqualTo(expectRequestUri);
            softAssertions.assertThat(actual.getHttpVersion()).isEqualTo(expectHttpVersion);
        });
    }
}
