package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.exception.NoSuchHeaderException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @DisplayName("존재하는 header를 조회한다.")
    @Test
    void 존재하는_header를_조회한다() {
        // given
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("Content-Type", "text/html");
        map.put("Content-Length", "2273");

        Headers headers = new Headers(map);

        // when
        String actual = headers.findByHeaderKey("Content-Type");

        // then
        assertThat(actual).isEqualTo("text/html");
    }

    @DisplayName("존재하지 않는 header를 조회할 경우 예외를 던진다.")
    @Test
    void 존재하지_않는_header를_조회할_경우_예외를_던진다() {
        // given
        Headers headers = new Headers(new LinkedHashMap<>(Collections.emptyMap()));

        // when & then
        assertThatThrownBy(() -> headers.findByHeaderKey("Content-Type"))
                .isInstanceOf(NoSuchHeaderException.class);
    }
}
