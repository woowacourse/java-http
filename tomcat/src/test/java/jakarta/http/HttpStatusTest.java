package jakarta.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpStatusTest {

    @Test
    @DisplayName("응답 메시지에 들어갈 문자열을 생성한다.")
    void message() {
        Assertions.assertAll(
                () -> assertThat(HttpStatus.OK.getDescription()).isEqualTo("200 OK"),
                () -> assertThat(HttpStatus.FOUND.getDescription()).isEqualTo("302 Found")
        );
    }
}
