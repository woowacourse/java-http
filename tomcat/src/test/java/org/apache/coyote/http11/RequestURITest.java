package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestURITest {

    @DisplayName("requestURI를 받아 path와 query를 분리한다.")
    @Test
    void requestURI를_받아_path와_query를_분리한다() {
        // given
        String requestURI = "/login?account=mat&password=password";

        // when
        RequestURI actual = new RequestURI(requestURI);

        // then
        assertAll(() -> {
            assertThat(actual.getPath()).isEqualTo("/login");
            assertThat(actual.getQueryParameterKey("account")).isEqualTo("mat");
            assertThat(actual.getQueryParameterKey("password")).isEqualTo("password");
        });
    }
}
