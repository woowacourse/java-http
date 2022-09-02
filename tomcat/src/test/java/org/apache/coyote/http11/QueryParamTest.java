package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.response.UserRequest;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamTest {

    @DisplayName("queryString을 기반으로 UserRequest를 생성한다.")
    @Test
    void makeMapForQueryString() {
        final QueryParam queryParam = new QueryParam("/login?account=gugu&password=password");

        final UserRequest userRequest = queryParam.toUserRequest("account", "password");
        assertAll(
                () -> assertThat(userRequest.getAccount()).isEqualTo("gugu"),
                () -> assertThat(userRequest.getPassword()).isEqualTo("password")
        );
    }

    @DisplayName("잘못된 param으로 UserRequest를 생성하여 예외가 발생한다.")
    @Test
    void queryParamNotFoundException() {
        final QueryParam queryParam = new QueryParam("/login?account=gugu&password=password");

        assertThatThrownBy(
                () -> queryParam.toUserRequest("account1", "password"))
                .hasMessageContaining("잘못된 query param 압니다.")
                .isInstanceOf(QueryParamNotFoundException.class);
    }
}
