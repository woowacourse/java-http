package jakarta.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderTest {

    @Test
    @DisplayName("헤더는 a:b 형식만 인식한다.")
    void singleFormat() {
        Header header = new Header(List.of("a-2"));

        Optional<String> result = header.get("a");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("단일 헤더를 조회한다.")
    void single() {
        Header header = new Header(List.of("a:2"));

        Optional<String> result = header.get("a");

        assertThat(result).hasValue("2");
    }

    @Test
    @DisplayName("여러 헤더를 읽는다.")
    void multi() {
        List<String> headers = List.of("a:1", "b:2", "c:3");
        Header header = new Header(headers);

        Assertions.assertAll(
                () -> assertThat(header.get("a")).hasValue("1"),
                () -> assertThat(header.get("b")).hasValue("2"),
                () -> assertThat(header.get("c")).hasValue("3")
        );
    }

    @Test
    @DisplayName("header 상수를 조회 키로 사용할 수 있다.")
    void enumKey() {
        Header header = new Header(List.of("Location:/admin"));

        Optional<String> result = header.get(HttpHeaderKey.LOCATION);

        assertThat(result).hasValue("/admin");
    }

    @Test
    @DisplayName("정의된 헤더를 추가할 수 있다.")
    void append() {
        Header header = Header.empty();

        header.append(HttpHeaderKey.LOCATION, "/admin");

        Optional<String> result = header.get(HttpHeaderKey.LOCATION);
        assertThat(result).hasValue("/admin");
    }

    @Test
    @DisplayName("사용자 정의 헤더를 추가할 수 있다.")
    void appendCustom() {
        Header header = Header.empty();

        header.append("a", "b");

        Optional<String> result = header.get("a");
        assertThat(result).hasValue("b");
    }
}
