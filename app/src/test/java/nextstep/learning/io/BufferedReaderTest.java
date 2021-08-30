package nextstep.learning.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class BufferedReaderTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("빈 문자열일 때 ready 반환값 테스트")
    void readyTest(String line, boolean expected) throws IOException {

        // given
        final BufferedReader bufferedReader = new BufferedReader(new StringReader(line));

        // when
        boolean ready = bufferedReader.ready();

        // then
        assertThat(ready).isEqualTo(expected);
    }

    private static Stream<Arguments> readyTest() {
        return Stream.of(
                Arguments.of("", true),
                Arguments.of(" ", true),
                Arguments.of("\r", true),
                Arguments.of("\n", true),
                Arguments.of("\r\n", true)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("빈 문자열일 때 readLine 반환값 테스트")
    void readLineTest(String line, String expected) throws IOException {

        // given
        final BufferedReader bufferedReader = new BufferedReader(new StringReader(line));

        // when
        String readLine = bufferedReader.readLine();

        // then
        assertThat(readLine).isEqualTo(expected);
    }

    private static Stream<Arguments> readLineTest() {
        return Stream.of(
                Arguments.of("", null),
                Arguments.of(" ", " "),
                Arguments.of("\r", ""),
                Arguments.of("\n", ""),
                Arguments.of("\r\n", "")
        );
    }
}
