package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.UnauthorizedController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.Renderer;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public enum RequestMapping {

    INSTANCE;

    private static final Map<String, Controller> CONTROLLERS = new ConcurrentHashMap<>();

    static {
        CONTROLLERS.put("/", new DefaultController());
        CONTROLLERS.put("/index", new ResourceController());
        CONTROLLERS.put("/login", new LoginController(new UserService()));
        CONTROLLERS.put("/register", new RegisterController(new UserService()));
        CONTROLLERS.put("/401", new UnauthorizedController());
    }

    public void extractHttpResponse(HttpRequest request, HttpResponse response) {
        RequestLine requestLine = request.getRequestLine();
        String path = requestLine.getPath();
        Controller controller = getController(path);
        try {
            controller.service(request, response);
        } catch (RuntimeException e) {
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .setRedirect("/500");
        }

        render(response);
    }

    private Controller getController(String path) {
        if (path.contains(".")) {
            return new ResourceController();
        }
        return CONTROLLERS.getOrDefault(path, new NotFoundController());
    }

    private void render(HttpResponse response) {
        String redirect = response.getRedirect();

        if (response.isFound()) {
            response.addHeader("Content-Type", MimeType.HTML.getContentType())
                    .addHeader("Location", redirect)
                    .setResponseBody(ResponseBody.empty());
            return;
        }

        Renderer renderer = Renderer.from(redirect);
        response.addHeader("Content-Type", renderer.getMimeType())
                .addHeader("Content-Length", renderer.getContentLength())
                .setResponseBody(renderer.getResponseBody());
    }

}
