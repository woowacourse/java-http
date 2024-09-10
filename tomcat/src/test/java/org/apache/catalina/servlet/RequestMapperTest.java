package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.body.HttpRequestBody;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.startline.HttpMethod;
import org.apache.coyote.http11.startline.HttpRequestLine;
import org.apache.coyote.http11.startline.RequestUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("요청을 처리할 적절한 컨트롤러를 반환한다.")
class RequestMapperTest {

    @DisplayName("정적 요청이 오면 ResourceController를 반환한다.")
    @Test
    void getController_static() {
        // given
        RequestMapper mapper = new RequestMapper();
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, new RequestUri("/index"), "HTTP/1.1"),
                new HttpHeaders(),
                HttpRequestBody.empty()
        );

        // when
        Controller controller = mapper.getController(request);

        // then
        assertThat(controller.getClass()).isEqualTo(ResourceController.class);
    }

    @DisplayName("동적 요청이 오면 RequestMapping 어노테이션의 값에 따라 컨트롤러를 반환한다.")
    @Test
    void getController_dynamic() {
        // given
        RequestMapper mapper = new RequestMapper();

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
        Controller loginController = mapper.getController(requestLogin);
        Controller registerController = mapper.getController(requestRegister);

        // then
        assertThat(loginController.getClass()).isEqualTo(LoginController.class);
        assertThat(registerController.getClass()).isEqualTo(RegisterController.class);
    }
}
