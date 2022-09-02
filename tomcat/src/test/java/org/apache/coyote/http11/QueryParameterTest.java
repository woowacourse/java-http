package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.QueryParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParameterTest {

    @DisplayName("두 개 이상의 파라미터를 받는 경우")
    @Test
    void from() {
        final String queryString = "username=east&password=password";
        final Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("username", "east");
        expectedParams.put("password", "password");

        QueryParameter queryParameter = QueryParameter.from(queryString);

        assertThat(queryParameter.getParams()).isEqualTo(expectedParams);
    }

    @DisplayName("한 개의 파라미터를 받는 경우")
    @Test
    void from_One_Parameter() {
        final String queryString = "username=east";
        final Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("username", "east");

        QueryParameter queryParameter = QueryParameter.from(queryString);

        assertThat(queryParameter.getParams()).isEqualTo(expectedParams);
    }

}
