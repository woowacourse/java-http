package org.apache.coyote.http11.request.requestLine.requestUri;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
