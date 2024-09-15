package org.apache.catalina.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RequestBodyTest {

    @DisplayName("body에서 &을 기준으로 =의 왼쪽을 키, 오른쪽을 값으로 파싱한다.")
    @Test
    void parse() {
        RequestBody requestBody = RequestBody.parse("account=gugu&password=password&email=hkkang%40woowahan.com");

        assertAll(
                () -> assertThat(requestBody.getBody().get("account")).isEqualTo("gugu"),
                () -> assertThat(requestBody.getBody().get("password")).isEqualTo("password"),
                () -> assertThat(requestBody.getBody().get("email")).isEqualTo("hkkang%40woowahan.com")
        );
    }
}
