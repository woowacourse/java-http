package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RequestUriTest {

    @Test
    @DisplayName("RequestUri에서 QueryParam을 파싱한다.")
    void parseQueryParams() {
        // given
        RequestUri requestUri = new RequestUri("/login?account=gugu&password=password");

        // when
        Map<String, Object> queryParams = requestUri.parseQueryParams();

        // then
        assertThat(queryParams.get("account")).isEqualTo("gugu");
        assertThat(queryParams.get("password")).isEqualTo("password");
    }

    @ParameterizedTest
    @CsvSource(value = {"/login : false", "/login?account=gugu&password=password : true"}, delimiter = ':')
    @DisplayName("QueryParameter가 포함된 URI인지 판단한다.")
    void isContainsQueryParam(final String requestUri, final boolean expected) {
        // given
        final RequestUri givenRequestUri = new RequestUri(requestUri);

        // when
        final boolean actual = givenRequestUri.isContainsQueryParam();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("QueryParameter를 제외한 URI를 반환한다.")
    void getRemovedQueryParamUri() {
        // given
        final RequestUri givenRequestUri = new RequestUri("/login?account=gugu&password=password");
        final String expected = "/login";

        // when
        final String actual = givenRequestUri.getRemovedQueryParamUri();

        // then
        assertThat(expected).isEqualTo(actual);
    }
}
