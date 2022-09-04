package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.util.LinkedHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @DisplayName("header를 추가한다.")
    @Test
    void header를_추가한다() {
        // given
        Headers headers = new Headers();
        headers.addHeader("Content-Type", "text/html");
        headers.addHeader("Content-Length", "12");

        // when
        LinkedHashMap<String, String> actual = headers.getValues();

        // then
        assertThat(actual).hasSize(2);
    }

    @DisplayName("header가 존재하는 경우 true를 반환한다.")
    @Test
    void header가_존재하는_경우_true를_반환한다() {
        // given
        Headers headers = new Headers();
        headers.addHeader("Content-Type", "text/html");

        // when
        boolean actual = headers.hasHeader("Content-Type");

        // then
        assertThat(actual).isTrue();
    }
}
