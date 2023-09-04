package org.apache.coyote.http11;

import org.apache.coyote.http11.controller.ErrorController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticController;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerAdapterTest {

    @DisplayName("Request에 해당하는 컨트롤러를 찾아온다.")
    @Test
    void findController() {
        //given
        final ControllerAdapter controllerAdapter = new ControllerAdapter();

        //when
        final String indexRequest = "GET /index.html HTTP/1.1";
        final String loginRequest = "GET /login HTTP/1.1";
        final String registerRequest = "GET /register HTTP/1.1";
        final String errorRequest = "GET /sd HTTP/1.1";

        //then
        SoftAssertions.assertSoftly(
                soft -> {
                    soft.assertThat(controllerAdapter.findController(new HttpRequest(indexRequest))).isInstanceOf(
                            StaticController.class);
                    soft.assertThat(controllerAdapter.findController(new HttpRequest(loginRequest))).isInstanceOf(
                            LoginController.class);
                    soft.assertThat(controllerAdapter.findController(new HttpRequest(registerRequest))).isInstanceOf(
                            RegisterController.class);
                    soft.assertThat(controllerAdapter.findController(new HttpRequest(errorRequest))).isInstanceOf(
                            ErrorController.class);
                }
        );
    }
}
