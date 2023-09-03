package org.apache.coyote.common;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HeadersTest {

    @Test
    void 헤더와_값이_하나의_문자열로_놓여진_상태의_목록을_이용하여_생성에_성공한다() {
        // given
        final List<String> headersWithValue = List.of(
                "Accept: text/html;charset=utf-8",
                "Connection: keep-alive"
        );

        // expect
        assertThatCode(() -> new Headers(headersWithValue))
                .doesNotThrowAnyException();
    }

    @Test
    void 헤더와_값이_키_값_대칭인_상태로_놓여진_자료구조를_이용하여_생성에_성공한다() {
        // given
        final Map<String, String> headersWithValue = Map.of(
                "Accept", "text/html;charset=utf-8",
                "Connection", "keep-alive"
        );

        // expect
        assertThatCode(() -> new Headers(headersWithValue))
                .doesNotThrowAnyException();
    }

    @Test
    void 헤더_이름_목록을_정렬하여_가져올_수_있다() {
        // given
        final List<String> headersWithValue = new ArrayList<>(List.of(
                "Connection: keep-alive",
                "Accept: text/html;charset=utf-8"
        ));

        // when
        final Headers headers = new Headers(headersWithValue);

        // then
        assertThat(headers.headerNames()).containsExactly("Accept", "Connection");
    }
}
