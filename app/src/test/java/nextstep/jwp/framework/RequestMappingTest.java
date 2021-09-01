package nextstep.jwp.framework;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpRequestLine;
import nextstep.jwp.framework.http.ProtocolVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @DisplayName("일반 요청의 컨트롤러를 생성한다.")
    @Test
    void createDefaultController() {
        //given
        RequestMapping requestMapping = RequestMapping.create();
        HttpRequestLine httpRequestLine = new HttpRequestLine(HttpMethod.GET, new HttpPath("/static/index.html"), new ProtocolVersion("HTTP/1.1"));

        //when
        Controller controller = requestMapping.getController(new HttpRequest(httpRequestLine, null, null));

        //then
        assertThat(controller).isInstanceOf(DefaultController.class);
    }

    @DisplayName("로그인 요청의 컨트롤러를 생성한다.")
    @Test
    void createLoginController() {
        //given
        RequestMapping requestMapping = RequestMapping.create();
        HttpRequestLine httpRequestLine = new HttpRequestLine(HttpMethod.GET, new HttpPath("/login.html"), new ProtocolVersion("HTTP/1.1"));

        //when
        Controller controller = requestMapping.getController(new HttpRequest(httpRequestLine, null, null));

        //then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @DisplayName("회원가입 요청의 컨트롤러를 생성한다.")
    @Test
    void createRegisterController() {
        //given
        RequestMapping requestMapping = RequestMapping.create();
        HttpRequestLine httpRequestLine = new HttpRequestLine(HttpMethod.GET, new HttpPath("/register.html"), new ProtocolVersion("HTTP/1.1"));

        //when
        Controller controller = requestMapping.getController(new HttpRequest(httpRequestLine, null, null));

        //then
        assertThat(controller).isInstanceOf(RegisterController.class);
    }
}
