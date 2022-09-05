package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.FileNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    @DisplayName("존재하지 않는 파일인 경우 예외가 발생한다.")
    @Test
    void notExistFileException() {
        final StaticResourceController staticResourceController = new StaticResourceController();
        final String startLine = "GET /login.css HTTP/1.1";

        assertThatThrownBy(() ->
                staticResourceController.run(startLine))
                .hasMessageContaining("해당 파일을 지원하지않습니다.")
                .isInstanceOf(FileNotFoundException.class);
    }
}
