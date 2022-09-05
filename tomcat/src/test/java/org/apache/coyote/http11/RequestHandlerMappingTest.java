package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RequestHandlerMappingTest {

    @DisplayName("url에 매칭이 되는 controller 반환")
    @ParameterizedTest
    @CsvSource(value = {
            "GET /index.html HTTP/1.1 : DashBoardController",
            "POST /login HTTP/1.1 : AuthController",
            "GET / HTTP/1.1 : HomeController",
            "GET /css/style.css HTTP/1.1 : StaticResourceController"}, delimiterString = " : "
    )
    void matchController(String startLine, String controllerName) {
        assertThat(RequestHandlerMapping.getHandler(startLine)
                .getClass()
                .getSimpleName())
                .isEqualTo(controllerName);
    }
}
