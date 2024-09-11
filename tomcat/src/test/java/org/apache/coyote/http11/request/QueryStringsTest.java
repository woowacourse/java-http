package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringsTest {

    @DisplayName("쿼리 문자를 파싱하여 key로 선택할 수 있다.")
    @Test
    void from() {
        QueryStrings queryStrings = QueryStrings.from("account=gugu&password=password");

        assertAll(
                () -> assertThat(queryStrings.getQuery("account")).isEqualTo("gugu"),
                () -> assertThat(queryStrings.getQuery("password")).isEqualTo("password")
        );
    }

    @DisplayName("키가 없으면 안된다.")
    @Test
    void getQuery() {
        QueryStrings queryStrings = QueryStrings.from("account=gugu&password=password");

        assertThatThrownBy(() -> queryStrings.getQuery("id"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
