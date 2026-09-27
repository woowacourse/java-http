package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import org.apache.catalina.Container;
import org.apache.catalina.Manager;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;

public class ApplicationConfig {
    private final Manager manager = new SessionManager();
    private final RequestMapping requestMapping = createRequestMapping();

    private static RequestMapping createRequestMapping() {
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.register("/", new RootController());
        requestMapping.register("/login", new LoginController());
        requestMapping.register("/register", new RegisterController());
        return requestMapping;
    }

    public Container container() {
        return new Container(manager, requestMapping);
    }
}
