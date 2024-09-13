package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void get() {
        RequestBody requestBody = new RequestBody("account=account&password=password&email=email");
        assertThat(requestBody.get("account")).isEqualTo("account");
    }
}
