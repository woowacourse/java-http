package nextstep.jwp.controller;

import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.controller.modelview.View;
import nextstep.jwp.httpmessage.ContentType;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import nextstep.jwp.httpmessage.httpresponse.HttpStatusCode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FrontController {

    private final Map<String, Controller> controllerMap = new HashMap<>();

    public FrontController() {
        initHandlerMapping();
    }

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String requestPath = httpRequest.getPath();
        final Controller controller = controllerMap.get(requestPath);

        if (ContentType.containValueByUrl(requestPath)) {
            View view = new View(requestPath);
            view.render(Collections.emptyMap(), httpRequest, httpResponse);
            return;
        }

        if (Objects.isNull(controller)) {
            httpResponse.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
            error(httpRequest, httpResponse);
            return;
        }

        try {
            ModelView modelView = controller.process(httpRequest, httpResponse);
            View view = new View(modelView.getViewName());
            view.render(modelView.getModel(), httpRequest, httpResponse);
        } catch (RuntimeException illegalArgumentException) {
            httpResponse.setHttpStatusCode(HttpStatusCode.UNAUTHORIZED);
            error(httpRequest, httpResponse);
        }
    }

    public void error(HttpRequest httpRequest, HttpResponse httpResponse) {
        View view = new View("/" + httpResponse.getHttpStatusCode().getValue() + ".html");
        view.render(Collections.emptyMap(), httpRequest, httpResponse);
    }

    private void initHandlerMapping() {
        controllerMap.put("/", new HelloController());
        controllerMap.put("/login", new UserLoginController());
        controllerMap.put("/register", new UserRegisterController());
    }
}
