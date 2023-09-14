package org.apache.catalina;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.MemberRegisterController;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static RequestMapping instance;
    private final Map<String, Controller> controllers = new HashMap();

    public static RequestMapping getInstance() {
        if (instance == null) {
            instance = new RequestMapping();
        }
        return instance;
    }

    private RequestMapping() {
        final AbstractController loginHandler = new LoginController();
        final AbstractController memberRegisterHandler = new MemberRegisterController();
        controllers.put("/login", loginHandler);
        controllers.put("/register", memberRegisterHandler);
    }

    public Controller getController(HttpRequest request) {
        String requestURI = request.getRequestLine().getRequestURI();
        return controllers.getOrDefault(requestURI, new DefaultController());
    }
}
