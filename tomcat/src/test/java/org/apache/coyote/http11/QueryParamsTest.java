package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.http.QueryParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    @DisplayName("쿼리 스트링의 key로 value를 조회한다.")
    void find() {
        QueryParams queryParams = QueryParams.of("account=gugu&password=password");

        assertThat(queryParams.find("account")).isEqualTo("gugu");
    }
}
