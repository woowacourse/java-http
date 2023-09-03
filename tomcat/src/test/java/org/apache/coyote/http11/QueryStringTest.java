package org.apache.coyote.http11;

import org.apache.coyote.http11.request.QueryString;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class QueryStringTest {

    @Test
    void QueryString_값을_파싱할_수_있다() {
        // given
        String url = "/login?account=gugu&password=password";

        // when
        QueryString queryString = QueryString.from(url);

        // then
        assertAll(
                () -> assertThat(queryString.getValues()).containsKey("account"),
                () -> assertThat(queryString.getValues()).containsKey("password"),
                () -> assertThat(queryString.getValues()).containsValue("gugu"),
                () -> assertThat(queryString.getValues()).containsKey("password")
        );
    }

}
