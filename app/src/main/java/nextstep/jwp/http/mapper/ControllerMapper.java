package nextstep.jwp.http.mapper;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.HtmlController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.http.message.request.HttpRequestMessage;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private static final Map<String, Controller> mappingInfos;
    private static final Controller htmlController = HtmlController.getInstance();
    private static final Controller notFoundController = new NotFoundController();

    static {
        mappingInfos = new HashMap<>();
        mappingInfos.put("/", new HelloController());
        mappingInfos.put("/login", new LoginController());
    }

    public ControllerMapper() {
    }

    public Controller matchController(HttpRequestMessage httpRequestMessage) {
        String requestUri = httpRequestMessage.getHeader().requestUri();
        if (isHtmlPath(requestUri)) {
            return htmlController;
        }
        return mappingInfos.getOrDefault(requestUri, notFoundController);
    }

    private boolean isHtmlPath(String uri) {
        return uri.endsWith(".html");
    }
}
