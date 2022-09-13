package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void getValue() {
        RequestBody requestBody = RequestBody.of("id=lala&password=1234");

        assertAll(
                () -> assertThat(requestBody.getValue("id")).isEqualTo("lala"),
                () -> assertThat(requestBody.getValue("password")).isEqualTo("1234")
        );
    }
}
