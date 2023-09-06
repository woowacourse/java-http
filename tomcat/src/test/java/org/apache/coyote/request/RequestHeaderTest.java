package org.apache.coyote.request;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class RequestHeaderTest {

    @Test
    void 헤더에_컨텐트_타입이_없는경우_컨텐트길이는_0이다() {
        final List<String> headers = List.of("Host: localhost:8080 ", "Connection: keep-alive ");
        final RequestHeader requestHeader = RequestHeader.from(headers);

        assertThat(requestHeader.getContentLength()).isZero();
    }
}
