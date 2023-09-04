package org.apache.front;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HelloWorldController;
import nextstep.jwp.controller.LoginController;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicController implements FrontController {

    private static final Map<String, Controller> urlMapper = new ConcurrentHashMap<>();


    static {
        urlMapper.put("/", new HelloWorldController());
        urlMapper.put("/login", new LoginController());
    }

    @Override
    public Response process(Request request) {
        if(! urlMapper.containsKey(request.getPath())){
            throw new IllegalArgumentException();
        }
        Controller controller = urlMapper.get(request.getPath());
        return controller.handle(request);
    }
}
