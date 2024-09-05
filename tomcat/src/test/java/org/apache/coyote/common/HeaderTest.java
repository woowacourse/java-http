package org.apache.coyote.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeaderTest {

    @Test
    @DisplayName("헤더 문자열 리스트는 null이 될 수 없다.")
    void createWithNull() {
        assertThatThrownBy(() -> new Header(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("헤더는 a=b 형식만 인식한다.")
    void singleFormat() {
        Header header = new Header(List.of("a-2"));

        Optional<String> result = header.get("a");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("단일 헤더를 조회한다.")
    void single() {
        Header header = new Header(List.of("a=2"));

        Optional<String> result = header.get("a");

        assertThat(result).hasValue("2");
    }

    @Test
    @DisplayName("여러 헤더를 읽는다.")
    void multi() {
        List<String> headers = List.of("a=1", "b=2", "c=3");
        Header header = new Header(headers);

        Assertions.assertAll(
                () -> assertThat(header.get("a")).hasValue("1"),
                () -> assertThat(header.get("b")).hasValue("2"),
                () -> assertThat(header.get("c")).hasValue("3")
        );
    }

    @Test
    @DisplayName("조회 키는 null이 될 수 없다.")
    void keyNonNull() {
        Header header = new Header(List.of("a=2"));

        assertThatThrownBy(() -> header.get(null))
                .isInstanceOf(NullPointerException.class);
    }
}
