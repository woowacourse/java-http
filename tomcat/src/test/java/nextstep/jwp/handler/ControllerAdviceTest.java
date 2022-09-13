package nextstep.jwp.handler;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.response.ResponseEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerAdviceTest {

    private final ControllerAdvice controllerAdvice = ControllerAdvice.getInstance();

    @Test
    @DisplayName("handleException 메소드는 입력 받은 예외에 따라 매핑된 html 페이지와 리다이렉트 상태 코드를 담은 ResponseEntity를 반환한다.")
    void handleException() {
        // when
        final ResponseEntity response = controllerAdvice.handleException(UnauthorizedException.class);

        // then
        assertAll(() -> {
            assertThat(response.getHttpStatus()).isEqualTo(FOUND);
            assertThat(response.getHttpHeader().getHeader("Location"))
                    .isEqualTo(UNAUTHORIZED.getStatusCode() + ".html");
        });
    }
}