package org.apache.front;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HelloWorldController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicController implements FrontController {

    private static final Map<String, Controller> urlMapper = new ConcurrentHashMap<>();


    static {
        urlMapper.put("/", new HelloWorldController());
        urlMapper.put("/login", new LoginController());
        urlMapper.put("/register", new RegisterController());
    }

    @Override
    public ResponseEntity process(final Request request) {
        if (!urlMapper.containsKey(request.getPath())) {
//            return new PathResponse(request.getPath(), HttpURLConnection.HTTP_OK, "OK");
            return null;
        }
        final Controller controller = urlMapper.get(request.getPath());
        return controller.handle(request);
    }
}
