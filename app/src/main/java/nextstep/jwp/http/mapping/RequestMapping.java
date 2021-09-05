package nextstep.jwp.http.mapping;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.app.ui.DefaultController;
import nextstep.jwp.app.ui.LoginController;
import nextstep.jwp.app.ui.RegisterController;
import nextstep.jwp.app.ui.ResourceController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.mvc.controller.Controller;

public class RequestMapping {

    public static final Map<String, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put("/", new DefaultController());
        CONTROLLERS.put("/login", new LoginController());
        CONTROLLERS.put("/register", new RegisterController());
    }

    public static Controller getController(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if(CONTROLLERS.containsKey(path)){
            return CONTROLLERS.get(path);
        }
        return new ResourceController();
    }
}
