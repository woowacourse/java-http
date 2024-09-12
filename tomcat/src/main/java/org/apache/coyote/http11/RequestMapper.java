package org.apache.coyote.http11;

import com.techcourse.controller.UserController;

public class RequestMapper {
    public static Handler getHandler(HttpRequest request) {
        String path = request.getPath();
        if (path.startsWith("/login") || path.startsWith("/register")) {
            return UserController.getInstance();
        }
        if (path.equals("/") || path.endsWith("html") || path.endsWith("css") || path.endsWith("js")) {
            return ResourceHandler.getInstance();
        }
        return null;
    }
}
