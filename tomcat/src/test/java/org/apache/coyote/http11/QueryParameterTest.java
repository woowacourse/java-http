package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.http11.request.QueryParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParameterTest {

    @DisplayName("쿼리스트링을 받아 파싱하여 반환한다.")
    @Test
    void getValue() {
        QueryParameter queryParameter = QueryParameter.from("account=gugu&password=password");

        Map<String, String> values = queryParameter.getValues();
        String accountValue = values.get("account");
        String passwordValue = values.get("password");

        assertAll(
                () -> assertThat(accountValue).isEqualTo("gugu"),
                () -> assertThat(passwordValue).isEqualTo("password")
        );
    }
}