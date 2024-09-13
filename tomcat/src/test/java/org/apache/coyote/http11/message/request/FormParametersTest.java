package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormParametersTest {

    @Test
    @DisplayName("key에 해당하는 form 파라미터가 존재하는 경우, 해당 값을 반환한다.")
    void getSingleValueByKeyTest() {
        // given
        FormParameters formParameters = new FormParameters(Map.of("key", List.of("value")));

        // when
        String value = formParameters.getSingleValueByKey("key");

        // then
        assertEquals("value", value);
    }

    @Test
    @DisplayName("key에 해당하는 form 파라미터가 존재하지 않는 경우, IllegalArgumentException을 던진다.")
    void getSingleValueByKeyWithoutKeyTest() {
        // given
        FormParameters formParameters = new FormParameters(Map.of());

        // when & then
        assertThatThrownBy(() -> formParameters.getSingleValueByKey("key"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("key에 해당되는 form 파라미터가 존재하지 않습니다.");
    }
}
