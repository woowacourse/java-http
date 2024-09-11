package org.apache.coyote.http11.path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {

    @Test
    @DisplayName("/로 시작하지 않으면, 자동으로 /가 붙는다.")
    void start_with_slash() {
        final String line = "create_page";
        final Path path = Path.from(line);
        assertThat(path.value()).isEqualTo("/create_page");
    }

    @Test
    @DisplayName("/를 기반으로 경로를 파싱한다.")
    void parse_with_slash() {
        final String line = "/create_page/sample";
        final Path path = Path.from(line);
        assertThat(path.next()).isEqualTo(Path.from("/sample"));
    }

    @Test
    @DisplayName("/가 더 없으면, 마지막으로 간주한다.")
    void last_if_not_contain_slash() {
        final String line = "sample";
        final Path path = Path.from(line);
        assertThat(path.last()).isTrue();
    }

    @Test
    @DisplayName("파싱할 경로가 없을 시 예외를 발생한다.")
    void throw_exception_when_parse_with_not_contain_slash() {
        final String line = "sample";
        final Path path = Path.from(line);
        assertThatThrownBy(() -> path.next())
                .isInstanceOf(PathParseException.class);
    }
}
