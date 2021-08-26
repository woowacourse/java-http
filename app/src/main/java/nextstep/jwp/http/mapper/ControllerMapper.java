package nextstep.jwp.http.mapper;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.HtmlController;
import nextstep.jwp.controller.NotFoundController;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private static final Map<String, Controller> mappingInfos;
    private static final Controller htmlController = new HtmlController();
    private static final Controller notFoundController = new NotFoundController();

    static {
        mappingInfos = new HashMap<>();
        mappingInfos.put("/", new HelloController());
    }

    public ControllerMapper() {
    }

    public Controller matchController(String uri) {
        if (isHtmlPath(uri)) {
            return htmlController;
        }
        return mappingInfos.getOrDefault(uri, notFoundController);
    }

    private boolean isHtmlPath(String uri) {
        return uri.endsWith(".html");
    }
}
