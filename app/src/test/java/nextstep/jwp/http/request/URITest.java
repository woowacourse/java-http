package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class URITest {

    @DisplayName("URI 객체를 생성한다.")
    @Test
    void create() {
        String path = "/index.html";
        URI uri = new URI(path, new QueryStrings());

        assertThat(uri.getPath()).isEqualTo(path);
        assertThat(uri.getQueryStrings().isEmpty()).isTrue();
    }

    @DisplayName("uri 경로가 적절하지 않으면 예외를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "hello"})
    void createFail(String input) {
        assertThatThrownBy(() -> new URI(input, new QueryStrings()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}