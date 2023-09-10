package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    @DisplayName("올바른 매핑인지 확인1")
    void getController1() {
        RequestMapping requestMapping = new RequestMapping();
        HttpRequest httpRequest = new HttpRequest(null, "/login", null, null);
        Controller controller = requestMapping.getController(httpRequest);

        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    @DisplayName("올바른 매핑인지 확인2")
    void getController2() {
        RequestMapping requestMapping = new RequestMapping();
        HttpRequest httpRequest = new HttpRequest(null, "/register", null, null);
        Controller controller = requestMapping.getController(httpRequest);

        assertThat(controller).isInstanceOf(RegisterController.class);
    }

}
