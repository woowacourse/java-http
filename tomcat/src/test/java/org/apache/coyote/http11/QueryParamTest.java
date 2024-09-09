package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamTest {

    @DisplayName("QueryString을 파싱할 수 있다.")
    @Test
    void parseQueryString() {
        // given
        String queryString = "account=gugu&password=password";

        // when
        QueryParam queryParam = new QueryParam(queryString);

        // then
        assertAll(
                () -> assertThat(queryParam.getValue("account")).isEqualTo("gugu"),
                () -> assertThat(queryParam.getValue("password")).isEqualTo("password")
        );
    }
}