package nextstep.jwp;

import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.MainController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.model.httpmessage.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Object> handlerMappingMaps = new HashMap<>();

    public RequestMapping() {
        initRequestMappings();
    }

    private void initRequestMappings() {
        handlerMappingMaps.put("/", new MainController());
        handlerMappingMaps.put("/login", new LoginController());
        handlerMappingMaps.put("/register", new RegisterController());
        handlerMappingMaps.put("/index", new IndexController());
    }

    public Object getHandler(HttpRequest request) {
        String requestURI = request.getPath();
        return handlerMappingMaps.get(requestURI);
    }
}
