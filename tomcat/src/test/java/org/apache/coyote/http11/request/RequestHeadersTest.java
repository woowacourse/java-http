package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    void getHeaderValue() {
        String key = "KEY";
        String value = "VALUE";
        Map<String, String> headers = Map.of(key, value);
        RequestHeaders requestHeaders = new RequestHeaders(headers);

        Optional<String> headerValue = requestHeaders.getHeaderValue(key);

        assertAll(
                () -> assertThat(headerValue).isPresent(),
                () -> assertThat(headerValue.get()).isEqualTo(value)
        );
    }
}
