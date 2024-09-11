package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @DisplayName("파라미터를 키, 값으로 나누어 저장한다.")
    @Test
    void createRequestBody() {
        RequestBody requestBody =
                new RequestBody("account=gugu&password=password");

        org.junit.jupiter.api.Assertions.assertAll(
                () -> assertThat(requestBody.findByKey("account")).isEqualTo("gugu"),
                () -> assertThat(requestBody.findByKey("password")).isEqualTo("password")
        );
    }
}
