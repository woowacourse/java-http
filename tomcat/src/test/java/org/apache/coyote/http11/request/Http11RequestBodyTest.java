package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class Http11RequestBodyTest {

    @Test
    @DisplayName("RequestBody를 생성할 수 있다.")
    void construct() {
        String requestBodyLine = "account=gugu&password=1234";
        assertThatCode(() -> Http11RequestBody.from(requestBodyLine))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("RequestBody에서 값을 가져올 수 있다.")
    void get() {
        String requestBodyLine = "account=gugu&password=1234";
        Http11RequestBody body = Http11RequestBody.from(requestBodyLine);

        assertThat(body.get("account")).isEqualTo("gugu");
        assertThat(body.get("password")).isEqualTo("1234");
    }
}
