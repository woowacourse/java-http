package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {

    @Test
    void hasSession() {
        // given
        List<String> headerLines = List.of("Cookie: JSESSIONID=1234");
        RequestHeader requestHeader = new RequestHeader(headerLines);

        // when
        boolean actual = requestHeader.hasSession();

        // then
        assertThat(actual).isTrue();
    }
}
