package nextstep.jwp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class JwpApplicationTest {

    @Test
    void main() {
        assertThatCode(() -> JwpApplication.main(new String[]{"test"}))
            .doesNotThrowAnyException();
    }
}
