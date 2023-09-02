package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.QueryParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParameterTest {

    @DisplayName("파라미터가 한 개일 경우")
    @Test
    void from_singleParam() {
        // given
        final String queryString = "account=dino";
        final Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("account", "dino");

        // when
        final QueryParameter queryParameter = QueryParameter.from(queryString);

        // then
        assertThat(queryParameter.getParams()).isEqualTo(expectedParams);
    }

    @DisplayName("파라미터가 두 개일 경우")
    @Test
    void from_doubleParam() {
        // given
        final String queryString = "account=dino&password=goodDino";
        final Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("account", "dino");
        expectedParams.put("password", "goodDino");

        // when
        final QueryParameter queryParameter = QueryParameter.from(queryString);

        // then
        assertThat(queryParameter.getParams()).isEqualTo(expectedParams);
    }

    @DisplayName("파라미터가 없을 경우")
    @Test
    void from_nonParam() {
        // given
        final String queryString = null;
        final Map<String, String> expectedParams = new HashMap<>();

        // when
        final QueryParameter queryParameter = QueryParameter.from(queryString);

        // then
        assertThat(queryParameter.getParams()).isEqualTo(expectedParams);
    }
}
