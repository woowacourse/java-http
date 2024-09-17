package jakarta.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParameterTest {

    @Test
    @DisplayName("쿼리 파라미터 문자열은 null이 될 수 있다.")
    void createWithNull() {
        QueryParameter queryParameter = new QueryParameter(null);

        assertThat(queryParameter.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("키값 페어는 a=b 형식만 인식한다.")
    void checkFormat() {
        QueryParameter queryParameter = new QueryParameter("a-2");

        assertThat(queryParameter.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("단일 키값 페어를 조회한다.")
    void single() {
        QueryParameter queryParameter = new QueryParameter("a=2");

        Optional<String> result = queryParameter.get("a");

        assertThat(result).hasValue("2");
    }

    @Test
    @DisplayName("여러 키값 페어를 읽는다.")
    void multi() {
        List<String> strings = List.of("a=1", "b=2", "c=3");
        String input = String.join("&", strings);
        QueryParameter queryParameter = new QueryParameter(input);

        Assertions.assertAll(
                () -> assertThat(queryParameter.get("a")).hasValue("1"),
                () -> assertThat(queryParameter.get("b")).hasValue("2"),
                () -> assertThat(queryParameter.get("c")).hasValue("3")
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"=&:0", "a:0", "a=1:1", "a=2&b=2:2", "a=2&a=2:1", "&a=1&:1", "a=1&b=2&c=3:3"}, delimiter = ':')
    @DisplayName("여러 표현식을 처리한다.")
    void parametrized(String source, int expectedSize) {
        QueryParameter queryParameter = new QueryParameter(source);

        int result = queryParameter.getSize();

        assertThat(result).isEqualTo(expectedSize);
    }
}
