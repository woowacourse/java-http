package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParametersTest {

    @Test
    @DisplayName("쿼리 파라미터 문자열을 올바르게 변환한다.")
    void createQueryParameters() {
        String query = "query=abc&woowa=course";
        QueryParameters queryParameters = new QueryParameters(query);
        assertThat(queryParameters.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("올바르지 않은 쿼리 파라미터가 존재하는 경우 무시된다.")
    void ignoreInvalidQueryParameters() {
        String query = "query=abc&woowa&&gugu=genius";
        QueryParameters queryParameters = new QueryParameters(query);
        assertThat(queryParameters.size()).isEqualTo(2);
    }
}
