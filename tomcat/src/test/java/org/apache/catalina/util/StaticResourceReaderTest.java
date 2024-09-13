package org.apache.catalina.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.apache.coyote.http11.response.ResponseFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StaticResourceReaderTest {

    @DisplayName("정적 리소스를 읽어온다.")
    @Test
    void read() {
        // given
        String path = "/500.html";

        // when
        ResponseFile file = StaticResourceReader.read(path);

        // then
        assertThat(file).isNotNull();
    }

    @DisplayName("정적 리소스가 존재하는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideTestCase")
    void isExist(String filePath, boolean expected) {
        assertThat(StaticResourceReader.isExist(filePath)).isEqualTo(expected);
    }

    static Stream<Arguments> provideTestCase() {
        return Stream.of(
                Arguments.of("/css/styles.css", true),
                Arguments.of("/404.html", true),
                Arguments.of("/index.html", true),
                Arguments.of("/notfound.html", false)
        );
    }

    @DisplayName("존재하지 않는 리소스를 읽으려고 시도하면 예외가 발생한다.")
    @Test
    void readNotExistResource() {
        // given
        String path = "/some/path/to/not/exist";

        // when & then
        assertThatThrownBy(() -> StaticResourceReader.read(path))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
