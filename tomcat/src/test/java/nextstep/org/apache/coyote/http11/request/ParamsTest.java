package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.apache.coyote.http11.request.Params;
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
        final String actual = params.find(name);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("파라미터를 조회할 수 없으면 에러가 발생한다")
    @ParameterizedTest
    @CsvSource(value = {"account=gugu, password"})
    void findException(final String query, final String name) {
        final Params params = Params.parse(query);
        assertThatThrownBy(() -> params.find(name))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("파라미터를 찾을 수 없습니다 : " + name);
    }
}
