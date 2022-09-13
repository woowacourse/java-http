package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class RequestParametersTest {

    @Test
    void of() {
        RequestParameters requestParameters = RequestParameters.of("account=account&password=&email=email");

        assertAll(() -> {
            assertThat(requestParameters.get("account")).isEqualTo("account");
            assertThat(requestParameters.get("password")).isEqualTo("");
            assertThat(requestParameters.get("email")).isEqualTo("email");
                }
        );
    }
}
