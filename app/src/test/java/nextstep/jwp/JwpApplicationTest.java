package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class JwpApplicationTest {

    @Test
    void main() {
        assertThatCode(() -> JwpApplication.main(new String[]{"test"}))
            .doesNotThrowAnyException();
    }
}