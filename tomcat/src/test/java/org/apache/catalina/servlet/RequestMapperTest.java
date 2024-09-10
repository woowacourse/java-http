package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.body.HttpRequestBody;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.startline.HttpMethod;
import org.apache.coyote.http11.startline.HttpRequestLine;
import org.apache.coyote.http11.startline.RequestUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

    @DisplayName("요청이 오면 RequestMapping 어노테이션의 값에 따라 컨트롤러를 반환한다.")
    @Test
    void getController_dynamic() {
        // given
        HttpRequest requestLogin = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/login"), "HTTP/1.1"),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );
        HttpRequest requestRegister = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, new RequestUri("/register"), "HTTP/1.1"),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when
        Controller loginController = RequestMapper.getController(requestLogin);
        Controller registerController = RequestMapper.getController(requestRegister);

        // then
        assertThat(loginController.getClass()).isEqualTo(LoginController.class);
        assertThat(registerController.getClass()).isEqualTo(RegisterController.class);
    }
}
