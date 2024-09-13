package org.apache.coyote.http11.response.body;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseBodyTest {

    @DisplayName("응답 바디 데이터의 바이트 길이를 계산한다.")
    @Test
    void getContentLengthTest() {
        assertAll(
                () -> assertThat(new ResponseBody("Hello world!").getContentLength()).isEqualTo("12"),
                () -> assertThat(new ResponseBody("재즈").getContentLength()).isEqualTo("6")
        );
    }

}
