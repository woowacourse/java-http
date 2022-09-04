package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @Test
    void header가_존재하지_않는_경우_빈_값이_저장된다() {
        // given
        List<String> headers = List.of();

        // when
        Headers requestHeader = Headers.of(headers);

        // then
        assertThat(requestHeader.getHeaders()).isEmpty();
    }

    @Test
    void header_입력값들의_key_value를_구분할_수_있다() {
        // given
        List<String> headers = List.of("key: value", "name: park");

        // when
        Headers requestHeader = Headers.of(headers);

        // then
        assertThat(requestHeader.getHeaders()).usingRecursiveComparison()
                .isEqualTo(Map.of("key", "value", "name", "park"));
    }

    @Test
    void header_입력값의_형식이_잘못된_경우_예외를_던진다() {
        // given
        List<String> headers = List.of("key:value", "name: park");

        // when & then
        assertThatThrownBy(() -> Headers.of(headers))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }
}
