package org.apache.coyote.request.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.techcourse.controller.LoginController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    @DisplayName("Request를 처리할 수 있는 Controller를 찾는다.")
    void findController() {
        HttpRequest httpRequest = HttpRequestFixture.GET_LOGIN_PATH_NO_COOKIE_REQUEST;

        RequestMapping requestMapping = new RequestMapping();
        Controller controller = requestMapping.findController(httpRequest).get();

        assertEquals(controller.getClass(), LoginController.class);
    }
}
