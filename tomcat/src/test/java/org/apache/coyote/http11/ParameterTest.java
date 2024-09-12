package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParameterTest {

    @DisplayName("등록된 값 이름으로 값을 가져올 수 있다.")
    @Test
    void getValueSuccess() {
        // given
        Parameter parameter = new Parameter(Map.of("name", "poke"));

        // when
        String value = parameter.getValue("name");

        // then
        assertThat(value).isEqualTo("poke");
    }

    @DisplayName("값을 가져오는데 실패하면 빈 값을 반환한다.")
    @Test
    void getValueFail() {
        // given
        Parameter parameter = new Parameter(Map.of("name", "poke"));

        // when
        String value = parameter.getValue("invalidName");

        // then
        assertThat(value).isEmpty();
    }
}