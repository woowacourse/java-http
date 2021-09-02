package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.http.SupportedContentType;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapper {
    private static Map<String, Controller> HANDLER_MAP = new HashMap<>();
    private static Map<String, Controller> RESOURCE_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put("/index", new IndexController());
        HANDLER_MAP.put("/register", new RegisterController());
        HANDLER_MAP.put("/login", new LoginController());
        HANDLER_MAP.put("NOT_FOUND", new NotFoundController());

        RESOURCE_MAP.put(".html", new ResourceController());
        RESOURCE_MAP.put(".css", new ResourceController());
        RESOURCE_MAP.put(".js", new ResourceController());
    }

    public Controller getController(HttpRequest request) {
        SupportedContentType contentType = SupportedContentType.getContentType(request);
        if (contentType == SupportedContentType.NOTFOUND) {
            return HANDLER_MAP.getOrDefault(request.getUri(), HANDLER_MAP.get("NOT_FOUND"));
        }
        return RESOURCE_MAP.getOrDefault(contentType.getExtention(), HANDLER_MAP.get("NOT_FOUND"));

    }
}
