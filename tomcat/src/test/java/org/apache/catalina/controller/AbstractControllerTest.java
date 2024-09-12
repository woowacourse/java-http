package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.request.RequestParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private AbstractController controller = new AbstractController() {
    };

    @DisplayName("필요한 파라미터를 가져올 수 있다.")
    @Test
    void getRequiredParameterTest() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        RequestParameters parameters = new RequestParameters(Map.of("id", "mangcho", "password", "1234"));
        HttpRequest request = new HttpRequest(requestLine, null, parameters, null, null);

        // when
        String id = controller.getRequiredParameter(request, "id");
        String password = controller.getRequiredParameter(request, "password");

        // then
        assertAll(
                () -> assertThat(id).isEqualTo("mangcho"),
                () -> assertThat(password).isEqualTo("1234")
        );
    }

    @DisplayName("필요한 파라미터를 없으면 예외를 던진다.")
    @Test
    void getRequiredParameterTest1() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        RequestParameters parameters = new RequestParameters(Map.of("id", "mangcho", "password", "1234"));
        HttpRequest request = new HttpRequest(requestLine, null, parameters, null, null);

        // when, then
        assertThatThrownBy(() -> controller.getRequiredParameter(request, "notExist"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("필요한 바디 필드를 가져올 수 있다.")
    @Test
    void getRequiredBodyFieldTest() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        RequestBody requestBody = new RequestBody(Map.of("id", "mangcho", "password", "1234"));
        HttpRequest request = new HttpRequest(requestLine, null, null, null, requestBody);

        // when
        String id = controller.getRequiredBodyField(request, "id");
        String password = controller.getRequiredBodyField(request, "password");

        // then
        assertAll(
                () -> assertThat(id).isEqualTo("mangcho"),
                () -> assertThat(password).isEqualTo("1234")
        );
    }

    @DisplayName("필요한 바디 필드를 없으면 예외를 던진다.")
    @Test
    void getRequiredBodyFieldTest1() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        RequestBody requestBody = new RequestBody(Map.of("id", "mangcho", "password", "1234"));
        HttpRequest request = new HttpRequest(requestLine, null, null, null, requestBody);

        // when, then
        assertThatThrownBy(() -> controller.getRequiredBodyField(request, "notExist"))
                .isInstanceOf(NoSuchElementException.class);
    }
}
