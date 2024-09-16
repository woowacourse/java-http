package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestBodyTest {

    @Test
    @DisplayName("requestBody를 생성한다.")
    void createRequestBody() {
        var result = new HttpRequestBody("account=gugu&password=password&email=hkkang@woowahan.com");

        assertThat(result.getQueryParameters().get("account")).isEqualTo("gugu");
        assertThat(result.getQueryParameters().get("password")).isEqualTo("password");
        assertThat(result.getQueryParameters().get("email")).isEqualTo("hkkang@woowahan.com");
    }
}
