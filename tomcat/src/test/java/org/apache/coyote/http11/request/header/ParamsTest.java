package org.apache.coyote.http11.request.header;

import org.apache.coyote.http11.request.body.Params;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParamsTest {

    @DisplayName("QueryParam 파싱해서 객체로 변환한다")
    @Test
    void create_params() {
        // given
        final String httpRequest = "account=test";

        // when
        Params params = Params.from(httpRequest);

        // then
        assertThat(params.hasParam("account")).isTrue();
    }

    @DisplayName("param이 비어있는지 확인한다")
    @Test
    void check_params_are_empty() {
        // given
        final String httpRequest = "account=test";

        // when
        Params params = Params.from(httpRequest);

        // then
        assertThat(params.isEmpty()).isFalse();
    }

    @DisplayName("해당 param이 존재하는지 확인한다")
    @Test
    void check_is_exist_param() {
        // given
        final String httpRequest = "account=test";

        // when
        Params params = Params.from(httpRequest);

        // then
        assertThat(params.hasParam("account")).isTrue();
    }
}
