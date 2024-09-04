package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {

    @Test
    @DisplayName("/로 시작하지 않으면, 자동으로 /가 붙는다.")
    void some() {
        final String line = "create_page";
        final Path path = Path.create(line);
        assertThat("/create_page").isEqualTo(path.value());
    }

    @Test
    @DisplayName("/를 기반으로 경로를 파싱한다.")
    void some1() {
        final String line = "/create_page/sample";
        final Path path = Path.create(line);
        assertThat(path.next()).isEqualTo(Path.create("/sample"));
    }

    @Test
    @DisplayName("/가 더 없으면, 마지막으로 간주한다.")
    void some2() {
        final String line = "sample";
        final Path path = Path.create(line);
        assertThat(path.last()).isTrue();
    }

    @Test
    @DisplayName("파싱할 경로가 없을 시 예외를 발생한다.")
    void some3() {
        final String line = "sample";
        final Path path = Path.create(line);
        assertThatThrownBy(() -> path.next())
                .isInstanceOf(UriParseException.class);
    }

}
