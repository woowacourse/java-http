package nextstep.jwp.server;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseStatus;

public class ControllerDispatcher {

    private static ControllerDispatcher instance;

    private ControllerDispatcher() {
    }

    public static ControllerDispatcher getInstance() {
        if (instance == null) {
            instance = new ControllerDispatcher();
        }
        return instance;
    }

    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        Controller controller = RequestMapping.getController(httpRequest.getPath());
        if (controller == null) {
            String path = getDefaultPath(httpRequest.getPath());
            httpResponse.status(HttpResponseStatus.OK);
            httpResponse.resource(path);
            return;
        }

        controller.service(httpRequest, httpResponse);
    }

    private String getDefaultPath(String path) {
        if ("/".equals(path)) {
            return "/index.html";
        }
        return path;
    }
}
