package nextstep.jwp.framework.http;

import java.util.Map;
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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class QueryTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("쿼리 파싱 테스트")
    public void queryParsingTest(String queryString, Map<String, String> expectedQuery) {

        // when
        final Query query = new Query(queryString);

        //then
        assertThat(query).usingRecursiveComparison().isEqualTo(new Query(expectedQuery));
    }

    private static Stream<Arguments> queryParsingTest() {
        return Stream.of(
                Arguments.of("account=gugu", Map.of("account", "gugu")),
                Arguments.of("account=gugu&password=password", Map.of("account", "gugu", "password", "password"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"=password", " =password", " = "})
    @DisplayName("키 혹은 밸류가 없을 경우 예외 발생")
    public void failParsingIfKeyOrValueIsNull(String queryString) {

        // when
        ThrowableAssert.ThrowingCallable callable = () -> new Query(queryString);

        //then
        assertThatIllegalArgumentException().isThrownBy(callable)
                                            .withMessage("쿼리의 키 혹은 밸류가 빈 값입니다.");
    }
}
