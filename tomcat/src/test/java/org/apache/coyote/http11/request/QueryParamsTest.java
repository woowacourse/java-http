package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @DisplayName("쿼리 스트링을 분석헤 QueryParams 객체를 생성한다.")
    @Test
    void from() {
        final QueryParams queryParams = QueryParams.from("key1=value1&key2=value2");
        assertAll(
                () -> assertThat(queryParams.containsKey("key1")).isTrue(),
                () -> assertThat(queryParams.containsKey("key2")).isTrue(),
                () -> assertThat(queryParams.get("key1")).isEqualTo("value1"),
                () -> assertThat(queryParams.get("key2")).isEqualTo("value2")
        );
    }
}
