package nextstep.jwp.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UriTest {

    @ParameterizedTest(name = "HttpRequest Header의 URI를 파싱한다.")
    @MethodSource
    void of(String path, String expectedUri) {
        //given
        Uri url = Uri.of(path);
        //when
        String actualUri = url.getUri();
        //then
        assertThat(actualUri).isEqualTo(expectedUri);
    }

    static Stream<Arguments> of() {
        return Stream.of(
                Arguments.of("/", "/"),
                Arguments.of("/login?account=yon&password=password", "/login"));
    }
}