package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.body.Parameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ParametersTest {

    @Test
    @DisplayName("버퍼에서 파라미터들을 파싱할 수 있다")
    void construct() {
        //given
        final String queryString = "test=hi&test2=salmon";

        //when
        final Parameters parameters = Parameters.from(queryString.toCharArray());

        //then
        assertSoftly(softAssertions -> {
            assertThat(parameters.getParameter("test")).isEqualTo("hi");
            assertThat(parameters.getParameter("test2")).isEqualTo("salmon");
        });
    }

    @Test
    @DisplayName("존재하지 않는 파라미터를 조회하면 null이 나온다")
    void getParameter() {
        //given
        final Parameters parameters = Parameters.empty();

        //when, then
        assertThat(parameters.getParameter("test")).isNull();
    }
}
