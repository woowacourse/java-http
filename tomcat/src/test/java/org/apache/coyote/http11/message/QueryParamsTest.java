package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    @DisplayName("RequestLine 으로부터 QueryParam을 생성한다.")
    void from() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /login?account=gugu&password=password HTTP/1.1");

        // when
        final QueryParams queryParams = QueryParams.from(requestLine);

        // then
        assertThat(queryParams.getValueOf("account")).isEqualTo("gugu");
        assertThat(queryParams.getValueOf("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("RequestLine 에 QueryString 이 없다면 빈 map이 생성된다.")
    void from_notExist() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");

        // when
        final QueryParams queryParams = QueryParams.from(requestLine);

        // then
        assertThat(queryParams.getParamsWithValue()).isEmpty();
    }
}
