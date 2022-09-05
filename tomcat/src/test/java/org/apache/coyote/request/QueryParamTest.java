package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamTest {

    @DisplayName("키와 값을 제대로 분리할 수 있다.")
    @Test
    void splitKeyAndValue() {
        // given
        String queryParam = "account=dwoo";

        // when
        final QueryParam sut = QueryParam.from(queryParam);

        // then
        assertThat(sut.isSameKey("account")).isTrue();
        assertThat(sut.getValue()).isEqualTo("dwoo");
    }
}
