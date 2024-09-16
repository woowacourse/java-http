package org.apache.coyote.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.ResourceHandler;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static final RequestMapping instance = new RequestMapping();

    private final Map<String, Controller> handlerMappings = new HashMap<>();

    private RequestMapping() {
        handlerMappings.put("/login", new LoginController());
        handlerMappings.put("/register", new RegisterController());
    }

    public static RequestMapping getInstance() {
        return instance;
    }

    public HttpResponse dispatch(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = new HttpResponse();

        String path = httpRequest.getRequestURL();

        if ((path.equals("/") || path.equals("/index")) || MimeType.isValidFileExtension(path)) {
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.handle(httpRequest, httpResponse);
            return httpResponse;
        }

        Controller controller = getHandler(path);
        controller.service(httpRequest, httpResponse);

        return httpResponse;
    }

    public Controller getHandler(String path) {
        Controller controller = handlerMappings.get(path);
        if (controller == null) {
            return new NotFoundController();
        }
        return controller;
    }
}
