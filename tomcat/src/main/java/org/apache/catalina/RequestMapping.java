package org.apache.catalina;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.EmptyController;
import org.apache.coyote.controller.LoginHandler;
import org.apache.coyote.controller.MemberRegisterHandler;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestMapping {

    private static RequestMapping instance;
    private final List<AbstractController> controllers = new ArrayList<>();

    public static RequestMapping getInstance() {
        if (instance == null) {
            instance = new RequestMapping();
        }
        return instance;
    }

    private RequestMapping() {
        final AbstractController loginHandler = new LoginHandler("/login");
        final AbstractController memberRegisterHandler = new MemberRegisterHandler("/register");
        controllers.add(loginHandler);
        controllers.add(memberRegisterHandler);
    }

    public Controller getController(HttpRequest request) {
        for (AbstractController controller : controllers) {
            if (controller.isMatch(request)) {
                return controller;
            }
        }
        return new EmptyController();
    }
}
