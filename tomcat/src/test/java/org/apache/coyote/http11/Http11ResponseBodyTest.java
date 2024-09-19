package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11ResponseBodyTest {

    @DisplayName("responseBody를 바탕으로 Http11ResponseBody를 만든다.")
    @Test
    void of() {
        String responseBodyString = "account=gugu&password=password";

        Http11ResponseBody responseBody = Http11ResponseBody.of(responseBodyString);

        assertThat(responseBody.getBody()).isEqualTo(responseBodyString);
    }
}
