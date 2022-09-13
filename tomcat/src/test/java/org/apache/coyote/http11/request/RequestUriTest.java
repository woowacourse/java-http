package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestUriTest {

    @Test
    void parseStaticFilePath() {
        RequestUri requestUri = RequestUri.of("/index");

        assertThat(requestUri.parseStaticFilePath()).isEqualTo("/index.html");
    }

    @Test
    void getResourcePath() {
        RequestUri requestUri = RequestUri.of("/index?account=account&password=password");

        assertThat(requestUri.getResourcePath()).isEqualTo("/index");
    }
}
