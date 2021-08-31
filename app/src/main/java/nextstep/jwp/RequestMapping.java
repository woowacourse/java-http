package nextstep.jwp;

import nextstep.jwp.controller.DefaultController;
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

//        handlerMappingMaps.put("/index.html", new DefaultController());
//        handlerMappingMaps.put("/401.html", new DefaultController());

        handlerMappingMaps.put("/index", new DefaultController());

//        handlerMappingMaps.put("/favicon.ico", new DefaultController());
//        handlerMappingMaps.put("/js/scripts.js", new DefaultController());
//        handlerMappingMaps.put("/assets/chart-area.js", new DefaultController());
//        handlerMappingMaps.put("/assets/chart-bar.js", new DefaultController());
//        handlerMappingMaps.put("/assets/chart-pie.js", new DefaultController());
//        handlerMappingMaps.put("/css/styles.css", new DefaultController());
    }

    public Object getHandler(HttpRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMaps.get(requestURI);
    }
}
