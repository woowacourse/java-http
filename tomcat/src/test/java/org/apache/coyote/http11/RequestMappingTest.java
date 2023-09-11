package org.apache.coyote.http11;

import nextstep.jwp.controller.*;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestMappingTest {

    @Test
    @DisplayName("getController() 메서드에 / url에 보내는 GET 요청을 입력하면 DefaultGetController 객체를 반환한다.")
    void getController_default_get() {
        //given
        final HttpRequest request = new HttpRequest(HttpRequestStartLine.from("GET / HTTP/1.1"), null, null);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(DefaultGetController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /login url에 보내는 GET 요청을 입력하면 LoginGetController 객체를 반환한다.")
    void getController_login_get() {
        //given
        final HttpRequest request = new HttpRequest(HttpRequestStartLine.from("GET /login HTTP/1.1"), null, null);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(LoginGetController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /login url에 보내는 POST 요청을 입력하면 LoginPostController 객체를 반환한다.")
    void getController_login_post() {
        //given
        final HttpRequest request = new HttpRequest(HttpRequestStartLine.from("POST /login HTTP/1.1"), null, null);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(LoginPostController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /register url에 보내는 GET 요청을 입력하면 RegisterGetController 객체를 반환한다.")
    void getController_register_get() {
        //given
        final HttpRequest request = new HttpRequest(HttpRequestStartLine.from("GET /register HTTP/1.1"), null, null);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(RegisterGetController.class);
    }

    @Test
    @DisplayName("getController() 메서드에 /register url에 보내는 POST 요청을 입력하면 RegisterPostController 객체를 반환한다.")
    void getController_register_post() {
        //given
        final HttpRequest request = new HttpRequest(HttpRequestStartLine.from("POST /register HTTP/1.1"), null, null);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(RegisterPostController.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET /test HTTP/1.1", "POST / HTTP/1.1"})
    @DisplayName("getController() 메서드에 매핑되는 컨트롤러가 존재하지 않는 요청을 입력하면 DefaultController 객체를 반환한다.")
    void getController(final String startLine) {
        //given
        final HttpRequest request = new HttpRequest(HttpRequestStartLine.from(startLine), null, null);

        //when
        Controller controller = RequestMapping.getController(request);

        //then
        Assertions.assertThat(controller).isInstanceOf(DefaultController.class);
    }

}