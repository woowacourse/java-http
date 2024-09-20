package org.apache.catalina.servlet;

import org.apache.catalina.session.SessionService;
import com.techcourse.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static RequestMapping INSTANCE;

    private UserService userService;
    private SessionService sessionService;

    private RequestMapping() {
        userService = new UserService();
        sessionService = new SessionService();
    }

    public static RequestMapping getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestMapping();
        }
        return INSTANCE;
    }

    public HttpServlet getServlet(HttpRequest request) {
        String path = request.getPath();

        switch (path) {
            case "/":
                return new HomeServlet();
            case "/login":
                return new LoginServlet(userService, sessionService);
            case "/register":
                return new RegisterServlet(userService);
            default:
                return new StaticResourceServlet();
        }
    }
}
