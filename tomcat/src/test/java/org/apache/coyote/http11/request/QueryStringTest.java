package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringTest {

    @Test
    @DisplayName("QueryString을 생성한다.")
    void createQueryString() {
        //given
        final String string = "account=gugu&password=password";

        //when
        final QueryString queryString = QueryString.from(string);

        //then
        final Map<String, String> expect = new HashMap<>();
        expect.put("account", "gugu");
        expect.put("password", "password");
        assertThat(queryString.getQueryStrings()).isEqualTo(expect);
    }
}
