package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    void query_string_파싱_테스트() {
        // given
        String queryString = "account=gugu&password=password";

        // when
        QueryParams queryParams = QueryParams.parseQueryParams(queryString);

        // then
        assertAll(
                () -> assertThat(queryParams.get("account")).isEqualTo("gugu"),
                () -> assertThat(queryParams.get("password")).isEqualTo("password")
        );
    }
}
