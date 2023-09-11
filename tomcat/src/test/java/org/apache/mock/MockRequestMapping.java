package org.apache.mock;

import nextstep.jwp.presentation.HomePageController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.LoginPageController;
import nextstep.jwp.presentation.RegisterPageController;
import org.apache.catalina.controller.ControllerMappingInfo;
import org.apache.catalina.controller.RequestMapping;

import static org.apache.coyote.request.HttpMethod.GET;
import static org.apache.coyote.request.HttpMethod.POST;

public abstract class MockRequestMapping {

    public static RequestMapping getMockRequestMapping() {
        return new RequestMapping()
                .putController(ControllerMappingInfo.of(GET, false, "/"), new HomePageController())
                .putController(ControllerMappingInfo.of(GET, false, "/login"), new LoginPageController())
                .putController(ControllerMappingInfo.of(GET, true, "/login"), new LoginController())
                .putController(ControllerMappingInfo.of(GET, false, "/register"), new RegisterPageController())
                .putController(ControllerMappingInfo.of(POST, false, "/register"), new RegisterPageController());
    }
}
