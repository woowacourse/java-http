package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
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
        final HttpHeader httpHeader = new HttpHeader("");
        final HttpBody httpBody = new HttpBody("");
        final HttpRequest httpRequest = new HttpRequest(startLine, httpHeader, httpBody);

        assertThat(RequestHandlerMapping.getHandler(httpRequest)
                .getClass()
                .getSimpleName())
                .isEqualTo(controllerName);
    }
}
