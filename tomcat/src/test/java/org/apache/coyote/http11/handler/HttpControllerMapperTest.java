package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.WelcomePageController;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpUrl;
import org.junit.jupiter.api.Test;

class HttpControllerMapperTest {

    @Test
    void findHandlerTest() {
        HttpControllerMapper httpControllerMapper = HttpHandlerMapperFactory.create();

        assertAll(
                () -> assertThat(
                        httpControllerMapper.findController(new HttpRequest(HttpMethod.GET, new HttpUrl("/"))))
                        .get()
                        .isInstanceOf(WelcomePageController.class),
                () -> assertThat(
                        httpControllerMapper.findController(new HttpRequest(HttpMethod.GET, new HttpUrl("/login"))))
                        .get()
                        .isInstanceOf(LoginController.class),
                () -> assertThat(
                        httpControllerMapper.findController(new HttpRequest(HttpMethod.POST, new HttpUrl("/login"))))
                        .get()
                        .isInstanceOf(LoginController.class),
                () -> assertThat(
                        httpControllerMapper.findController(new HttpRequest(HttpMethod.GET, new HttpUrl("/register"))))
                        .get()
                        .isInstanceOf(RegisterController.class),
                () -> assertThat(
                        httpControllerMapper.findController(new HttpRequest(HttpMethod.POST, new HttpUrl("/register"))))
                        .get()
                        .isInstanceOf(RegisterController.class)
        );
    }
}
