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
import org.apache.catalina.http.startline.RequestUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

    @DisplayName("요청이 오면 RequestMapping 어노테이션의 값에 따라 컨트롤러를 반환한다.")
    @Test
    void getController_dynamic() {
        // given
        HttpRequest requestLogin = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );
        HttpRequest requestRegister = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/register"), HttpVersion.HTTP11),
                new HttpHeaders(),
                new HttpRequestBody()
        );

        // when
        Controller loginController = RequestMapper.getController(requestLogin);
        Controller registerController = RequestMapper.getController(requestRegister);

        // then
        assertThat(loginController.getClass()).isEqualTo(LoginController.class);
        assertThat(registerController.getClass()).isEqualTo(RegisterController.class);
    }
}
