package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.QueryString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpUriTest {

    @Test
    @DisplayName("요청 URI을 통해 쿼리 스트링과 분리하여 저장할 수 있다.")
    void createHttpUriWithQueryString() {
        //given
        String requestUri = "/login?account=account&password=password";

        //when
        HttpUri httpUri = HttpUri.from(requestUri);

        assertThat(httpUri.hasQueryString()).isTrue();
        QueryString queryString = httpUri.getQueryString();
        assertThat(httpUri.getNativePath()).isEqualTo("/login");
        assertThat(queryString.get("account")).isEqualTo("account");
        assertThat(queryString.get("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("요청 URI을 통해 쿼리 스트링과 분리하여 저장할 수 있다.")
    void createHttpUriWithoutQueryString() {
        //given
        String requestUri = "/login";

        //when
        HttpUri httpUri = HttpUri.from(requestUri);

        assertThat(httpUri.hasQueryString()).isFalse();
        assertThat(httpUri.getNativePath()).isEqualTo("/login");
    }

}