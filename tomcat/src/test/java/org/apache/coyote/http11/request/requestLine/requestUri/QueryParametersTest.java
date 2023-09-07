package org.apache.coyote.http11.request.requestLine.requestUri;

import org.apache.coyote.http11.common.header.HttpCookie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParametersTest {

    @Test
    @DisplayName("문자열로 주어진 쿼리 파라미터의 name과 value를 Map의 형태로 저장한다.")
    void queryStringToMap() {
        // given
        final String queryString = "name=encho&age=23";

        // when
        final QueryParameters actual = QueryParameters.from(queryString);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getQueryParameters()).hasSize(2);
            softAssertions.assertThat(actual.getQueryParameters().get("name")).isEqualTo("encho");
            softAssertions.assertThat(actual.getQueryParameters().get("age")).isEqualTo("23");
        });
    }

    @Test
    @DisplayName("쿼리파라미터가 비어있으면 true를 반환한다.")
    void isEmpty_true() {
        // given
        final QueryParameters queryParameters = QueryParameters.from("");

        // when
        final boolean actual = queryParameters.isEmpty();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("쿼리파라미터가 존재하면 false를 반환한다.")
    void isEmpty_false() {
        // given
        final String queryString = "account=gugu";
        final QueryParameters queryParameters = QueryParameters.from(queryString);

        // when
        final boolean actual = queryParameters.isEmpty();

        // then
        assertThat(actual).isFalse();
    }
}
