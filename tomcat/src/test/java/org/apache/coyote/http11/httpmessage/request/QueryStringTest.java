package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringTest {

    @Test
    @DisplayName("문자열 배열로부터 queryString을 생성한다.")
    void make_query_string_from_string_array() {
        // given
        final String[] queryStrings = new String[2];
        queryStrings[0] = "account=ako";
        queryStrings[1] = "password=password";

        // when
        final QueryString result = QueryString.fromRequest(queryStrings);

        // then
        assertThat(result.getQueryString()).hasSize(2);
        assertThat(result.getQueryString()).containsEntry("account", "ako");
        assertThat(result.getQueryString()).containsEntry("password", "password");
    }
}
