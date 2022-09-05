package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.http.domain.StatusCode;
import org.junit.jupiter.api.Test;

class StatusCodeTest {

    @Test
    void getStatusCode() {
        String statusCode = StatusCode.OK.getStatusCode();

        assertThat(statusCode).isEqualTo("200 OK");
    }
}
