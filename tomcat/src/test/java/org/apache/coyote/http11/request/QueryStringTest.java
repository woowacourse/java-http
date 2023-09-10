package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringTest {

    @Test
    @DisplayName("입력으로부터 QueryString을 추출해낼 수 있다.")
    void createQueryString() {
        //given
        String input = "account=gugu&password=gugupassword";

        //when
        QueryString queryString = QueryString.from(input);

        //then
        assertThat(queryString.hasValues()).isTrue();
        assertThat(queryString.get("account")).isEqualTo("gugu");
        assertThat(queryString.get("password")).isEqualTo("gugupassword");
    }

}