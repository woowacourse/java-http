package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.controller.LoginGetController;
import com.techcourse.controller.LoginPostController;
import com.techcourse.controller.RegisterGetController;
import com.techcourse.controller.RegisterPostController;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpUrl;
import org.junit.jupiter.api.Test;

class HttpHandlerMapperTest {

    @Test
    void findHandlerTest() {
        HttpHandlerMapper httpHandlerMapper = HttpHandlerMapperFactory.create();

        assertAll(
                () -> assertThat(
                        httpHandlerMapper.findHandler(new HttpRequest(HttpMethod.GET, new HttpUrl("/"))))
                        .get()
                        .isInstanceOf(StringHttpHandler.class),
                () -> assertThat(
                        httpHandlerMapper.findHandler(new HttpRequest(HttpMethod.GET, new HttpUrl("/login"))))
                        .get()
                        .isInstanceOf(LoginGetController.class),
                () -> assertThat(
                        httpHandlerMapper.findHandler(new HttpRequest(HttpMethod.POST, new HttpUrl("/login"))))
                        .get()
                        .isInstanceOf(LoginPostController.class),
                () -> assertThat(
                        httpHandlerMapper.findHandler(new HttpRequest(HttpMethod.GET, new HttpUrl("/register"))))
                        .get()
                        .isInstanceOf(RegisterGetController.class),
                () -> assertThat(
                        httpHandlerMapper.findHandler(new HttpRequest(HttpMethod.POST, new HttpUrl("/register"))))
                        .get()
                        .isInstanceOf(RegisterPostController.class)
        );
    }
}
