package org.apache.front;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HelloWorldController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.apache.exception.PageRedirectException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicController implements FrontController {

    private static final Map<String, Controller> urlMapper = new ConcurrentHashMap<>();

    private static final DynamicController dynamicController = new DynamicController();

    static {
        urlMapper.put("/", new HelloWorldController());
        urlMapper.put("/login", new LoginController());
        urlMapper.put("/register", new RegisterController());
    }

    public static DynamicController singleTone() {
        return dynamicController;
    }

    private DynamicController(){
    }

    @Override
    public ResponseEntity process(final Request request) {
        if (!urlMapper.containsKey(request.getPath())) {
            throw new PageRedirectException.PageNotFound(request.httpVersion());
        }
        final Controller controller = urlMapper.get(request.getPath());
        return controller.handle(request);
    }
}
