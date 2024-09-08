package org.apache.coyote.http11.queryparam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParamsTest {
    final String line = "account=gugu&password=password";
    final QueryParams queryParam = QueryParams.from(line);

    @Test
    @DisplayName("각각의 쿼리는 &를 기반으로 구분된다.")
    void seperate_each_query_with_ampersand() {
        final var account = queryParam.getQueryParam("account");
        final var password = queryParam.getQueryParam("password");
        assertThat(account).isNotNull();
        assertThat(password).isNotNull();
    }

    @Test
    @DisplayName("쿼리는 =를 기반으로 키와 값을 분류한다.")
    void seperate_query_with_ampersand() {
        final var account = queryParam.getQueryParam("account");
        final var password = queryParam.getQueryParam("password");
        assertThat(account).isEqualTo("gugu");
        assertThat(password).isEqualTo("password");
    }
}
