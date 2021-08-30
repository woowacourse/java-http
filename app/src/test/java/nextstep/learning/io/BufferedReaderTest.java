package nextstep.learning.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class BufferedReaderTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("")
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
    @DisplayName("")
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

    @ParameterizedTest
    @MethodSource
    @DisplayName("")
    void readLineExceptionTest(String line, boolean whetherExceptionThrow) {

        // given
        final BufferedReader bufferedReader = new BufferedReader(new StringReader(line));

        // when
        ThrowableAssert.ThrowingCallable callable = bufferedReader::readLine;

        // then
        if (whetherExceptionThrow) {
            assertThatCode(callable).isExactlyInstanceOf(IOException.class);
        } else {
            assertThatCode(callable).doesNotThrowAnyException();
        }
    }

    private static Stream<Arguments> readLineExceptionTest() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of(" ", false),
                Arguments.of("\r", false),
                Arguments.of("\n", false),
                Arguments.of("\r\n", false)
        );
    }
}
