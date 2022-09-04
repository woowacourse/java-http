package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringTest {

    @DisplayName("쿼리 스트링을 전달하면 객체가 생성된다.")
    @Test
    void constructor() {
        // given
        String queryString = "key1=value1&key2=value2";

        // when
        QueryString actual = new QueryString(queryString);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("쿼리 스트링의 파싱 결과를 가져올 수 있다.")
    @Test
    void getQuery() {
        // given
        QueryString queryString = new QueryString("key1=value1&key2=value2");

        // when
        String actual1 = queryString.getQuery("key1").get();
        String actual2 = queryString.getQuery("key2").get();

        // then
        assertAll(() -> {
            assertThat(actual1).isEqualTo("value1");
            assertThat(actual2).isEqualTo("value2");
        });
    }
}
