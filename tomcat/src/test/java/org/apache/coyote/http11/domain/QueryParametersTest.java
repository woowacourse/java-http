package org.apache.coyote.http11.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.web.QueryParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParametersTest {

    @DisplayName("Query String 파싱이 제대로 이루어지는지 확인한다.")
    @Test
    void from() {
        // given
        final String queryString = "name=sun&type=backend&campus=jamsil";

        // when
        final QueryParameters queryParameters = QueryParameters.from(queryString);

        // then
        assertAll(
                () -> assertThat(queryParameters.getValueByKey("name")).isEqualTo("sun"),
                () -> assertThat(queryParameters.getValueByKey("type")).isEqualTo("backend"),
                () -> assertThat(queryParameters.getValueByKey("campus")).isEqualTo("jamsil")
        );
    }
}
