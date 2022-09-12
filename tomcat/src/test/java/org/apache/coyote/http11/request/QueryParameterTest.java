package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParameterTest {

    @Test
    @DisplayName("정적 팩토리 메소드는 쿼리 스트링 입력을 쿼리 파라미터로 파싱하여 queryParameters에 저장한다.")
    void from() {
        // given
        final String queryString = "name=eve&account=2yujeong&place=jamsil";

        // when
        final QueryParameter queryParameter = QueryParameter.from(queryString);

        // then
        assertAll(() -> {
            assertThat(queryParameter.getParameter("name")).isEqualTo("eve");
            assertThat(queryParameter.getParameter("account")).isEqualTo("2yujeong");
            assertThat(queryParameter.getParameter("place")).isEqualTo("jamsil");
        });
    }
}