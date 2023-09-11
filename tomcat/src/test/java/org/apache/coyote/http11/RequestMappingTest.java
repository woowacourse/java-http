package org.apache.coyote.http11;

import nextstep.jwp.controller.*;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.request.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

class RequestMappingTest {

    @Test
    @DisplayName("getController() 메서드에 / url에 보내는 GET 요청을 입력하면 DefaultGetController 객체를 반환한다.")
    void getController_default_get() throws IOException {
        //given
        final BufferedReader reader = new BufferedReader(
                new StringReader(String.join("\r\n",
                        "GET / HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "",
                        "")
                ));
        final HttpRequest request = HttpRequest.from(reader);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(DefaultGetController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /login url에 보내는 GET 요청을 입력하면 LoginGetController 객체를 반환한다.")
    void getController_login_get() throws IOException {
        //given
        final BufferedReader reader = new BufferedReader(
                new StringReader(String.join("\r\n",
                        "GET /login HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "",
                        "")
                ));
        final HttpRequest request = HttpRequest.from(reader);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(LoginGetController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /login url에 보내는 POST 요청을 입력하면 LoginPostController 객체를 반환한다.")
    void getController_login_post() throws IOException {
        //given
        final BufferedReader reader = new BufferedReader(
                new StringReader(String.join("\r\n",
                        "POST /login HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "",
                        "")
                ));
        final HttpRequest request = HttpRequest.from(reader);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(LoginPostController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /register url에 보내는 GET 요청을 입력하면 RegisterGetController 객체를 반환한다.")
    void getController_register_get() throws IOException {
        //given
        final BufferedReader reader = new BufferedReader(
                new StringReader(String.join("\r\n",
                        "GET /register HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "",
                        "")
                ));
        final HttpRequest request = HttpRequest.from(reader);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(RegisterGetController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /register url에 보내는 POST 요청을 입력하면 RegisterPostController 객체를 반환한다.")
    void getController_register_post() throws IOException {
        //given
        final BufferedReader reader = new BufferedReader(
                new StringReader(String.join("\r\n",
                        "POST /register HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "",
                        "")
                ));
        final HttpRequest request = HttpRequest.from(reader);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(RegisterPostController.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET /test HTTP/1.1", "POST / HTTP/1.1"})
    @DisplayName("getController() 메서드에 매핑되는 컨트롤러가 존재하지 않는 요청을 입력하면 DefaultController 객체를 반환한다.")
    void getController(final String startLine) throws IOException {
        //given
        final BufferedReader reader = new BufferedReader(
                new StringReader(String.join("\r\n",
                        startLine + " ",
                        "Host: localhost:8080 ",
                        "",
                        "")
                ));
        final HttpRequest request = HttpRequest.from(reader);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(DefaultController.class);
    }

}