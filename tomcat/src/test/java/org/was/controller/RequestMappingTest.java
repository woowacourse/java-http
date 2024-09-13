package org.was.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.controller.DashboardController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.HashMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.Test;
import org.was.controller.exception.ControllerNotFoundException;

class RequestMappingTest {

    @Test
    void 기본_경로_요청을_처리하는_컨트롤러를_반환() {
        // given
        String path = "/";
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        RequestMapping requestMapping = RequestMapping.getInstance();

        // when
        Controller actual = requestMapping.getController(request);

        // then
        assertThat(actual).isInstanceOf(DashboardController.class);
    }

    @Test
    void 로그인_경로_요청을_처리하는_컨트롤러를_반환() {
        // given
        String path = "/login";
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        RequestMapping requestMapping = RequestMapping.getInstance();

        // when
        Controller actual = requestMapping.getController(request);

        // then
        assertThat(actual).isInstanceOf(LoginController.class);
    }

    @Test
    void 회원가입_경로_요청을_처리하는_컨트롤러를_반환() {
        // given
        String path = "/register";
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        RequestMapping requestMapping = RequestMapping.getInstance();

        // when
        Controller actual = requestMapping.getController(request);

        // then
        assertThat(actual).isInstanceOf(RegisterController.class);
    }

    @Test
    void 지원하지_않는_경로를_요청할_경우_예외_발생() {
        // given
        String path = "/nonExistentResource";
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        RequestMapping requestMapping = RequestMapping.getInstance();

        // when, then
        assertThatThrownBy(() -> requestMapping.getController(request))
                .isInstanceOf(ControllerNotFoundException.class);
    }
}
