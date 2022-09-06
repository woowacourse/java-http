package org.apache.coyote.http11.httpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.httpRequest.QueryString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QueryStringTest {

    @DisplayName("queryString을 파싱해준다.")
    @Test
    void parsingUrl() {
        // given
        String value = "account=tonic&password=password";

        // when
        QueryString queryString = QueryString.from(value);
        // then
        assertAll(
                () -> assertThat(queryString.get("account")).isEqualTo("tonic"),
                () -> assertThat(queryString.get("password")).isEqualTo("password")
        );
    }
}
