package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParametersTest {

    @DisplayName("queryString을 받아 분리한다.")
    @Test
    void queryString을_받아_path와_query를_분리한다() {
        // given
        String queryString = "account=mat&password=password";

        // when
        RequestParameters actual = new RequestParameters(queryString);

        // then
        assertAll(() -> {
            assertThat(actual.getParameter("account")).isEqualTo("mat");
            assertThat(actual.getParameter("password")).isEqualTo("password");
        });
    }
}
