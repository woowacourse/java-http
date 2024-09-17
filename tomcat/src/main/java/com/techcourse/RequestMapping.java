package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.Controller;
import org.apache.catalina.resource.ResourceHandler;
import org.apache.coyote.http11.data.HttpRequest;

public class RequestMapping {
    public static Controller getController(HttpRequest request) {
        String path = request.getPath();
        if (path.equals("/login")) {
            return LoginController.getInstance();
        }
        if (path.equals("/register")) {
            return RegisterController.getInstance();
        }
        if (path.equals("/") || path.endsWith("html") || path.endsWith("css") || path.endsWith("js")) {
            return ResourceHandler.getInstance();
        }
        return null;
    }
}
