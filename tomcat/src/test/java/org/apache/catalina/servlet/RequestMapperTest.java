package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.body.HttpRequestBody;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpMethod;
import org.apache.catalina.http.startline.HttpRequestLine;
import org.apache.catalina.http.startline.HttpVersion;
import org.apache.catalina.http.startline.RequestURI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

    @DisplayName("요청이 오면 RequestMapping 어노테이션의 값에 따라 컨트롤러를 반환한다.")
    @Test
    void getController() {
        // given
        HttpRequest requestLogin = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );
        HttpRequest requestRegister = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestURI("/register"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when
        AbstractController loginController = RequestMapper.getController(requestLogin);
        AbstractController registerController = RequestMapper.getController(requestRegister);

        // then
        assertThat(loginController.getClass()).isEqualTo(LoginController.class);
        assertThat(registerController.getClass()).isEqualTo(RegisterController.class);
    }

    @DisplayName("요청에 응답할 컨트롤러가 없다면 ResourceController를 반환한다.")
    @Test
    void getController_noneMatch() {
        // given
        HttpRequest requestResource = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestURI("/index"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when
        AbstractController resourceController = RequestMapper.getController(requestResource);

        // then
        assertThat(resourceController.getClass()).isEqualTo(ResourceController.class);
    }
}
