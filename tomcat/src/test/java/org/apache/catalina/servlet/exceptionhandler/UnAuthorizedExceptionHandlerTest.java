package org.apache.catalina.servlet.exceptionhandler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnAuthorizedExceptionHandlerTest {

    @Test
    @DisplayName("핸들링되면 401.html을 반환한다.")
    void return_401_dot_html_when_handling() {
        // given
        final var unAuthorizedExceptionHandler = new UnAuthorizedExceptionHandler();

        // when
        final var response = unAuthorizedExceptionHandler.handle();

        // then
        assertThat(response.getHeaderContent("Location")).isEqualTo("http://localhost:8080/401.html");
    }
}
