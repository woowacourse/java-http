package nextstep.jwp.framework.http;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class URITest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("URI 파싱 테스트")
    public void uriParsingTest(String givenUri, URI expected) {

        // when
        final URI uri = new URI(givenUri);

        //then
        assertThat(uri).usingRecursiveComparison().isEqualTo(expected);
    }

    private static Stream<Arguments> uriParsingTest() {
        return Stream.of(
                Arguments.of(
                        "/login",
                        new URI("/login", new Query())
                ),
                Arguments.of(
                        "/login?account=gugu",
                        new URI("/login", new Query(Map.of("account", "gugu")))
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/login?"})
    @DisplayName("쿼리가 없을 경우 URI를 Path로 사용")
    public void failParsingIfKeyOrValueIsNull(String givenUri) {

        // when
        final URI uri = new URI(givenUri);

        //then
        final URI expected = new URI("/login", new Query());
        assertThat(uri).usingRecursiveComparison().isEqualTo(expected);
    }
}
