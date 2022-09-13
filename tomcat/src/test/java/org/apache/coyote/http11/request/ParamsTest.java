package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class ParamsTest {

    @DisplayName("URI Query로부터 파라미터 쌍을 파싱한다")
    @ParameterizedTest
    @MethodSource("provideForParse")
    void parse(final String query, final Map<String, String> expected) {
        final Params params = Params.parse(query);
        final Map<String, String> actual = params.getParams();

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForParse() {
        return Stream.of(
                Arguments.of(
                        "account=gugu&password=password",
                        Map.of("account", "gugu", "password", "password")),
                Arguments.of(
                        "param1=qwe&param2=123&param=eee",
                        Map.of("param1", "qwe", "param2", "123", "param", "eee")),
                Arguments.of(
                        "param1=qwe$param2=123param=eee",
                        Map.of("param1", "qwe$param2=123param=eee"))
        );
    }

    @DisplayName("파라미터를 조회한다")
    @ParameterizedTest
    @CsvSource(value = {"account=gugu, account, gugu"})
    void find(final String query, final String name, final String expected) {
        final Params params = Params.parse(query);
        final Optional<String> actual = params.find(name);

        assertThat(actual).hasValue(expected);
    }

    @DisplayName("존재하지 않는 파라미터를 조회한다")
    @ParameterizedTest
    @CsvSource(value = {"account=gugu, password"})
    void findException(final String query, final String name) {
        final Params params = Params.parse(query);
        final Optional<String> actual = params.find(name);

        assertThat(actual).isEmpty();
    }
}
