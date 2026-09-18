package org.apache.coyote.http11.request;

import org.apache.coyote.http11.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpQueryParamsTest {
    @Test
    void parseQueryString() {
        // given
        final String queryString = "account=gugu&password=password";

        // when
        final HttpQueryParams queryParams = HttpQueryParams.from(queryString);

        // then
        assertThat(queryParams.get("account")).isEqualTo("gugu");
        assertThat(queryParams.get("password")).isEqualTo("password");
    }

    @Test
    void decodeQueryString() {
        // given
        final String queryString = "name=%EA%B5%AC%EA%B5%AC&message=hello+world";

        // when
        final HttpQueryParams queryParams = HttpQueryParams.from(queryString);

        // then
        assertThat(queryParams.get("name")).isEqualTo("구구");
        assertThat(queryParams.get("message")).isEqualTo("hello world");
    }

    @Test
    void invalidPercentEncoding() {
        // given
        final String queryString = "account=%";

        // when & then
        assertThatThrownBy(() -> HttpQueryParams.from(queryString))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("잘못된 쿼리 스트링입니다.");
    }
}
