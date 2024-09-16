package jakarta.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HttpXW3UrlEncodedBodyTest {

    @Test
    @DisplayName("urlEncodedBody는 null이 될 수 있다.")
    void createWithNull() {
        HttpBody urlEncodedBody = new HttpXW3UrlEncodedBody(null);

        assertThat(urlEncodedBody.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("키값 페어는 a=b 형식만 인식한다.")
    void checkFormat() {
        HttpBody urlEncodedBody = new HttpXW3UrlEncodedBody("a-2".toCharArray());

        assertThat(urlEncodedBody.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("단일 키값 페어를 조회한다.")
    void single() {
        HttpBody urlEncodedBody = new HttpXW3UrlEncodedBody("a=2".toCharArray());

        Optional<String> result = urlEncodedBody.get("a");

        assertThat(result).hasValue("2");
    }

    @Test
    @DisplayName("여러 키값 페어를 읽는다.")
    void multi() {
        List<String> strings = List.of("a=1", "b=2", "c=3");
        String input = String.join("&", strings);
        HttpBody urlEncodedBody = new HttpXW3UrlEncodedBody(input.toCharArray());

        Assertions.assertAll(
                () -> assertThat(urlEncodedBody.get("a")).hasValue("1"),
                () -> assertThat(urlEncodedBody.get("b")).hasValue("2"),
                () -> assertThat(urlEncodedBody.get("c")).hasValue("3")
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"=&:0", "a:0", "a=1:1", "a=2&b=2:2", "a=2&a=2:1", "&a=1&:1", "a=1&b=2&c=3:3"}, delimiter = ':')
    @DisplayName("여러 표현식을 처리한다.")
    void parametrized(String source, int expectedSize) {
        HttpBody urlEncodedBody = new HttpXW3UrlEncodedBody(source.toCharArray());

        int result = urlEncodedBody.getSize();

        assertThat(result).isEqualTo(expectedSize);
    }
}
