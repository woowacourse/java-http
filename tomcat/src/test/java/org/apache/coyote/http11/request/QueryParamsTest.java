package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.vo.QueryParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class QueryParamsTest {

    @Test
    @DisplayName("유효한 key로 queryParam 조회 시 적절한 값을 반환한다.")
    void validKeyReturnValidValue() {
        // given
        String query = "account=gugu&password=password";
        QueryParams queryParams = new QueryParams(query);

        // when
        String account = queryParams.getValue("account");
        String password = queryParams.getValue("password");

        // then
        assertThat(account).isEqualTo("gugu");
        assertThat(password).isEqualTo("password");
    }

    @Test
    @DisplayName("유효하지 않은 key로 queryParam 조회 시 Null을 반환한다.")
    void invalidKeyReturnNull() {
        // given
        String query = "account=gugu&password=password";
        QueryParams queryParams = new QueryParams(query);

        // when
        String actual = queryParams.getValue("InvalidKey");

        // then
        assertThat(actual).isNull();
    }

}
