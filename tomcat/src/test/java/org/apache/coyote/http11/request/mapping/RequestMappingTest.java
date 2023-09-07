package org.apache.coyote.http11.request.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HomeController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.ResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestMappingTest {

    static Stream<Arguments> resourceArguments() {
        return Stream.of(
                Arguments.of("/index.html", ContentType.TEXT_HTML.getValue(), ResourceController.class),
                Arguments.of("/styles.css", ContentType.TEXT_CSS.getValue(), ResourceController.class),
                Arguments.of("/script.js", ContentType.APPLICATION_JAVASCRIPT.getValue(), ResourceController.class)
        );
    }

    static Stream<Arguments> requestArguments() {
        return Stream.of(
                Arguments.of("/", HomeController.class),
                Arguments.of("/register", RegisterController.class),
                Arguments.of("/login", LoginController.class)
        );
    }


    @ParameterizedTest
    @MethodSource("resourceArguments")
    @DisplayName("RequestURI에 Extension이 존재하면 ResourceController를 반환한다.")
    void getController(final String requestUri, final String contentType, Class<Controller> expectedControllerClass) throws IOException {
        // given
        String startLine = "GET " + requestUri + " HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: " + contentType,
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        // when
        Controller actualController = RequestMapping.getController(request);

        // then
        assertThat(actualController).isInstanceOf(expectedControllerClass);
    }

    @ParameterizedTest
    @MethodSource("requestArguments")
    @DisplayName("RequestURI에 Extension이 존재하지 않으면 RequestURI에 맞는 RequestController를 반환한다.")
    void getController(final String requestUri, Class<Controller> expectedControllerClass) throws IOException {
        // given
        String startLine = "GET " + requestUri + " HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        // when
        Controller actualController = RequestMapping.getController(request);

        // then
        assertThat(actualController).isInstanceOf(expectedControllerClass);
    }

    @Test
    @DisplayName("RequestURI에 맵핑되는 컨트롤러가 없으면 예외가 발생한다.")
    void getController() throws IOException {
        // given
        String notMatchRequestUri = "notMatch";
        String startLine = "GET " + notMatchRequestUri + " HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        // when & then
        assertThatThrownBy(() -> RequestMapping.getController(request))
                .isInstanceOf(HttpRequestException.NotFoundMappingController.class)
                .hasMessage("Request URI에 매핑되는 Controller를 찾을 수 없습니다.");
    }
}
