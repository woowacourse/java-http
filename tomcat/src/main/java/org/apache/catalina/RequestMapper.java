package org.apache.catalina;

import com.techcourse.controller.LoginController;
import java.util.Map;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.request.HttpRequest;

public class RequestMapper {

    private final Map<String, Controller> controllers;

    // TODO: map을 외부에서 등록할 수 있도록 개선 필요
    public RequestMapper(SessionManager sessionManager) {
        this.controllers = Map.of(
                "/login", new LoginController(sessionManager)
        );
    }

    // TODO
//    public void register(String path, Controller controller) {
//        controllers.put(path, controller);
//    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        return new DefaultController();
    }
}
