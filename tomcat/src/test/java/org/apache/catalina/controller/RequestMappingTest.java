package org.apache.catalina.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.IOException;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.coyote.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestTestSupport;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    @DisplayName("요청에 알맞는 컨트롤러를 반환한다.")
    void getController() throws IOException {
        HttpRequest home = HttpRequestTestSupport.homeGet();
        HttpRequest login = HttpRequestTestSupport.loginGet();
        HttpRequest register = HttpRequestTestSupport.registerGet();

        assertAll(
                () -> assertInstanceOf(HomeController.class, requestMapping.getController(home)),
                () -> assertInstanceOf(LoginController.class, requestMapping.getController(login)),
                () -> assertInstanceOf(RegisterController.class, requestMapping.getController(register))
        );
    }
}
