package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Test
    void 처리가능한_컨트롤러가_존재하는_URI라면_매핑된_Controller를_반환한다() {
        FrontController frontController = FrontController.getInstance();

        Controller indexController = frontController.mapController("/index");
        Controller loginController = frontController.mapController("/login");
        Controller registerController = frontController.mapController("/register");
        Controller welcomeController = frontController.mapController("/");

        assertThat(indexController).isExactlyInstanceOf(IndexController.class);
        assertThat(loginController).isExactlyInstanceOf(LoginController.class);
        assertThat(registerController).isExactlyInstanceOf(RegisterController.class);
        assertThat(welcomeController).isExactlyInstanceOf(WelcomeController.class);
    }
}
