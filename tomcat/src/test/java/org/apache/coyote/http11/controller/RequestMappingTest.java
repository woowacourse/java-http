package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class RequestMappingTest {

    @DisplayName("컨트롤러 갖고 오기 실패 : path가 없을 경우")
    @Test
    void getController_nullPath_exception() {
        // given
        HttpRequest httpRequest = new HttpRequest(null, null, null, null, null, null);

        // when & then
        assertThatThrownBy(
                () -> RequestMapping.getController(httpRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("path is null");
    }

    @DisplayName("컨트롤러 갖고 오기 : HomeController")
    @Test
    void getController_homeController() {
        // given
        HttpRequest httpRequest = new HttpRequest(null, "/", null, null, null, null);

        // when
        Controller controller = RequestMapping.getController(httpRequest);

        // then
        assertThat(controller).isInstanceOf(HomeController.class);
    }

    @DisplayName("컨트롤러 갖고 오기 : LoginController")
    @Test
    void getController_loginController() {
        // given
        HttpRequest httpRequest = new HttpRequest(null, "/login", null, null, null, null);

        // when
        Controller controller = RequestMapping.getController(httpRequest);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @DisplayName("컨트롤러 갖고 오기 : RegisterController")
    @Test
    void getController_registerController() {
        // given
        HttpRequest httpRequest = new HttpRequest(null, "/register", null, null, null, null);

        // when
        Controller controller = RequestMapping.getController(httpRequest);

        // then
        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @DisplayName("컨트롤러 갖고 오기 : PageController")
    @Test
    void getController_pageController() {
        // given
        HttpRequest httpRequest = new HttpRequest(null, "/index.html", null, null, null, null);

        // when
        Controller controller = RequestMapping.getController(httpRequest);

        // then
        assertThat(controller).isInstanceOf(PageController.class);
    }
}
