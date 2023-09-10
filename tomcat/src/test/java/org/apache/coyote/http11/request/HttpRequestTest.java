package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;
import org.assertj.core.api.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

class HttpRequestTest {

    @Test
    void from() throws IOException {
        // given
        final String request = "GET /login HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive";

        final BufferedReader bufferedReader = new BufferedReader(new StringReader(request));

        // when
        final HttpRequest actual = HttpRequest.from(bufferedReader);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            final RequestLine requestLine = actual.getStartLine();
            final RequestHeaders requestHeaders = actual.getHeaders();

            softAssertions.assertThat(requestLine.getMethod()).isEqualTo(Method.GET);
            softAssertions.assertThat(requestLine.getUri()).isEqualTo("/login");
            softAssertions.assertThat(requestLine.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
            softAssertions.assertThat(requestHeaders.getHeaders().get(0).getName()).isEqualTo("Host");
            softAssertions.assertThat(requestHeaders.getHeaders().get(0).getValue()).isEqualTo("localhost:8080");
            softAssertions.assertThat(requestHeaders.getHeaders().get(1).getName()).isEqualTo("Connection");
            softAssertions.assertThat(requestHeaders.getHeaders().get(1).getValue()).isEqualTo("keep-alive");
        });
    }
}
