package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import com.techcourse.config.UnauthorizedInterceptor;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.httprequest.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final UnauthorizedInterceptor unauthorizedInterceptor;

    public RequestMapping() {
        this.unauthorizedInterceptor = new UnauthorizedInterceptor();
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest httpRequest) {
        if (unauthorizedInterceptor.isInterceptPath(httpRequest)) {
            throw new UnauthorizedException("권한이 없는 페이지로의 접근입니다");
        }

        String path = httpRequest.getPath();
        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        throw new NotFoundException("존재하지 않는 경로입니다.");
    }
}
