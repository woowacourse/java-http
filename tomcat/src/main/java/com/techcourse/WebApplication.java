package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.StaticResource;
import org.apache.coyote.http11.session.SessionManager;

public final class WebApplication {

    private WebApplication() {
    }

    public static RequestMapping createRequestMapping() {
        StaticResource staticResource = new StaticResource();
        SessionManager sessionManager = new SessionManager();
        RequestMapping requestMapping = new RequestMapping(new StaticResourceController(staticResource));

        requestMapping.add("/", new RootController());
        requestMapping.add("/login", new LoginController(sessionManager, staticResource));
        requestMapping.add("/register", new RegisterController(staticResource));
        return requestMapping;
    }
}
