package org.apache.catalina;

import com.techcourse.controller.Controller;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ResponseCreator;

import java.io.IOException;

public class HandlerAdapter {
    private final ResponseCreator responseCreator;
    private final Controller controller;

    public HandlerAdapter() {
        this.responseCreator = new ResponseCreator();
        this.controller = new Controller();
    }

    public String handle(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.getPath();
        String httpMethod = httpRequest.getRequestMethod();
        if ("GET".equals(httpMethod)) {
            if (path.equals("/")) {
                return controller.getHelloWorldPage(httpRequest);
            }
            if (path.equals("/index.html")) {
                return controller.getDefaultPage(httpRequest);
            }
            if (path.equals("/login")) {
                return controller.getLoginPage(httpRequest);
            }
        }
        return responseCreator.create(200, path);
    }
}
