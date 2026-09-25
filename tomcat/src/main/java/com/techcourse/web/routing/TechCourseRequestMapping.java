package com.techcourse.web.routing;

import org.apache.catalina.controller.Controller;
import com.techcourse.web.controller.LoginController;
import com.techcourse.web.controller.LogoutController;
import com.techcourse.web.controller.RegisterController;
import com.techcourse.web.controller.StaticResourceController;
import com.techcourse.web.resource.StaticResourceHandler;
import org.apache.catalina.routing.RequestMapping;
import org.apache.coyote.http11.HttpRequest;

public class TechCourseRequestMapping implements RequestMapping {

    private final StaticResourceHandler staticResourceHandler;

    public TechCourseRequestMapping(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    public Controller getController(HttpRequest request) {
        if ("/login".equals(request.getPathUri())) {
            return new LoginController(staticResourceHandler);
        }

        if ("/logout".equals(request.getPathUri())){
            return new LogoutController();
        }

        if ("/register".equals(request.getPathUri())){
            return new RegisterController(staticResourceHandler);
        }

        return new StaticResourceController(staticResourceHandler);
    }
}
