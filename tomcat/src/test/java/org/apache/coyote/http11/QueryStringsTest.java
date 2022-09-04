package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringsTest {

    @Test
    @DisplayName("쿼리 스트링의 key로 value를 조회한다.")
    void find() {
        QueryStrings queryStrings = QueryStrings.of("account=gugu&password=password");

        assertThat(queryStrings.find("account")).isEqualTo("gugu");
    }
}
