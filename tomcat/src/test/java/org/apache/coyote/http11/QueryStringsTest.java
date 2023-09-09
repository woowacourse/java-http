package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringsTest {

    @DisplayName("쿼리 스트링 문자열을 입력 받아 객체를 생성한다.")
    @Test
    void from() {
        // given
        final String rawQueryString = "account=gugu&password=password";

        // when
        final QueryStrings queryStrings = QueryStrings.from(rawQueryString);

        // then
        assertAll(
            () -> assertEquals("gugu", queryStrings.getValueByName("account")),
            () -> assertEquals("password", queryStrings.getValueByName("password"))
        );
    }

    @DisplayName("빈 문자열, null 이 들어오면 빈 객체를 생성한다.")
    @Test
    void createEmptyObject() {
        // given
        final String rawQueryString = "    ";

        // when
        final QueryStrings queryStrings = QueryStrings.from(rawQueryString);

        // then
        assertTrue(queryStrings.getValues().isEmpty());
    }

    @DisplayName("쿼리 스트링 키 값을 조회할 때 값이 존재하지 않으면 예외를 던진다.")
    @Test
    void notExistedKey() {
        // given
        final String rawQueryString = "account=gugu&password=password";
        final QueryStrings queryStrings = QueryStrings.from(rawQueryString);

        // when
        // then
        assertThatThrownBy(() -> queryStrings.getValueByName("notExistedKey"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
