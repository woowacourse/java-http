package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    private final RequestHeaders headers = RequestHeaders.from(List.of(
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 12",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
    ));

    @Test
    void mappingHeader() {
        assertAll(
                () -> assertThat(headers.getValue().get("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getValue().get("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(headers.getValue().get("Content-Length")).isEqualTo("12")
        );
    }

    @Test
    void getContentLength() {
        assertThat(headers.getContentLength()).isEqualTo(12);
    }

    @Test
    void getCookie() throws IllegalAccessException {
        assertThat(headers.getCookie()).isNotNull();
    }
}
