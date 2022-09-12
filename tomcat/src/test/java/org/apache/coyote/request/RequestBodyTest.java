package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void requestBody를_생성한다() {
        RequestBody requestBody = RequestBody.from("account=gugu&password=password");

        assertAll(
                () -> assertThat(requestBody.getValue("account")).isEqualTo("gugu"),
                () -> assertThat(requestBody.getValue("password")).isEqualTo("password")
        );
    }

    @Test
    void 올바르지_않은_requestBody이면_예외가_발생한다() {
        RequestBody requestBody = RequestBody.from("password=password");

        assertThatThrownBy(() -> requestBody.getValue("account"))
                .isInstanceOf(InvalidRequestException.class);
    }
}
