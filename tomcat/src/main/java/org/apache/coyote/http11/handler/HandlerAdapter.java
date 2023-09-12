package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.controller.HelloWorldController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ViewController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;

public class HandlerAdapter {

    private static HandlerAdapter instance;

    private static final Map<MethodPath, Controller> controllers = new HashMap<>();

    public static HandlerAdapter getInstance(){
        if(instance==null){
            synchronized (HandlerAdapter.class){
                instance = new HandlerAdapter();
            }
        }
        return instance;
    }

    static  {
        addController(HttpMethod.POST, "/login", new LoginController());
        addController(HttpMethod.POST, "/register", new RegisterController());
        addController(HttpMethod.GET, "/login", new LoginController());
        addController(HttpMethod.GET, "/register", new RegisterController());
        addController(HttpMethod.GET, "/", new HelloWorldController());
    }

    public Controller mapping(Request request) {
        MethodPath requestInfo
                = new MethodPath(request.getMethod(), request.getPath());
        if (request.getPath().contains(".")) {
            return new ViewController();
        }
        return controllers.entrySet().stream()
                .filter(entry -> entry.getKey().equals(requestInfo))
                .findFirst()
                .map(Entry::getValue)
                .orElseThrow(NoSuchApiException::new);
    }

    private static void addController(HttpMethod httpMethod, String path, Controller controller) {
        controllers.put(new MethodPath(httpMethod, path), controller);
    }
}
