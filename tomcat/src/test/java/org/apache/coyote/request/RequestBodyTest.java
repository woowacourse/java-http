package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @DisplayName("생성 성공")
    @Test
    void construct_Success() {
        RequestBody requestBody = new RequestBody("name=lee&age=20");
        assertAll(
                () -> assertThat(requestBody.get("name")).isEqualTo("lee"),
                () -> assertThat(requestBody.get("age")).isEqualTo("20")
        );
    }

    @DisplayName("생성 실패: 올바르지 않은 형식")
    @Test
    void construct_Fail() {
        assertThatThrownBy(() -> new RequestBody("name=lee&age20"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("올바르지 않은 Request Body 형식입니다.");
    }

}
