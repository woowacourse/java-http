package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    void queryParams을_생성한다() {
        QueryParams queryParams = QueryParams.from("account=gugu&password=password");

        assertAll(
                () -> assertThat(queryParams.getValue("account")).isEqualTo("gugu"),
                () -> assertThat(queryParams.getValue("password")).isEqualTo("password")
        );
    }

    @Test
    void 올바르지_않은_queryParams이면_예외가_발생한다() {
        QueryParams queryParams = QueryParams.from("password=password");
        assertThatThrownBy(() -> queryParams.getValue("account"))
                .isInstanceOf(InvalidRequestException.class);
    }
}
