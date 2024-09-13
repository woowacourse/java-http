package org.apache.catalina.servlet.exceptionhandler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.resource.StaticResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotFoundExceptionHandlerTest {

    @Test
    @DisplayName("핸들링되면 404.html을 반환한다.")
    void return_404_dot_html_when_handling() {
        // given
        final var notFoundExceptionHandler = new NotFoundExceptionHandler();

        // when
        final var response = notFoundExceptionHandler.handle();

        // then
        assertThat(response).isEqualTo(StaticResourceFinder.render("404.html"));
    }
}
