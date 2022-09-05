package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @DisplayName("키를 통해서 원하는 키의 값을 구할 수 있다.")
    @Test
    void findQueryParamFromKey() {
        // given
        final String queryString = "account=dwoo&password=123";
        final QueryParams sut = QueryParams.from(queryString);

        // when
        final String account = sut.getValueFromKey("account");
        final String password = sut.getValueFromKey("password");

        // then
        assertThat(account).isEqualTo("dwoo");
        assertThat(password).isEqualTo("123");
    }

    @DisplayName("만약 쿼리스트링에 포함되지 않은 키로 조회시 예외를 던진다.")
    @Test
    void failToFindQueryParam() {
        // given
        final String queryString = "account=dwoo&password=123";
        final QueryParams sut = QueryParams.from(queryString);

        // when & then
        assertThatThrownBy(() -> sut.getValueFromKey("invalidKey"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 키입니다.");
    }
}
