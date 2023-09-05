package org.apache.coyote.http11.handler.controller.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DictTest {

    @DisplayName("path를 이용해서 Enum을 가져온다")
    @Test
    void returns_enum_using_path() {
        // given
        String path = "/index";

        // when
        Dict dict = Dict.find(path);

        // then
        assertThat(dict).isEqualTo(Dict.INDEX);
    }

    @DisplayName("path가 잘못된다면 예외를 발생한다")
    @Test
    void throws_exception_when_path_invalid() {
        // given
        String path = "/index-invalid";

        // when & then
        assertThatThrownBy(() -> Dict.find(path))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
