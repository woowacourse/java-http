package com.techcourse.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http.request.RequestParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParamsTest {

    @DisplayName("RequestParams 생성")
    @Test
    void fromTest1() {
        // given
        String queryString = "name=test&age=25&city=seoul";

        // when
        RequestParams requestParams = RequestParams.from(queryString);

        // then
        assertThat(requestParams.queryParameters()).hasSize(3);
        assertThat(requestParams.queryParameters().get("name")).isEqualTo("test");
        assertThat(requestParams.queryParameters().get("age")).isEqualTo("25");
        assertThat(requestParams.queryParameters().get("city")).isEqualTo("seoul");
    }

    @DisplayName("잘못된 형식인 경우")
    @Test
    void fromTest2() {
        // given
        String invalidQueryString = "invalid-format";

        // when & then
        assertThatThrownBy(() -> RequestParams.from(invalidQueryString))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("쿼리 파라미터의 형식은 'key=value' 이여야 합니다.");
    }
}
