package org.apache.coyote.http11.request;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class QueryStringTest {

    @Test
    void from() {
        // given
        final String queryString = "account=gugu&password=password";


        // when
        final QueryString actual = QueryString.from(queryString);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            final Map<String, String> queries = actual.getQueries();
            softAssertions.assertThat(queries.get("account")).isEqualTo("gugu");
            softAssertions.assertThat(queries.get("password")).isEqualTo("password");
        });
    }
}
