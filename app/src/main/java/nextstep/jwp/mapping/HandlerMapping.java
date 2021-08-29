package nextstep.jwp.mapping;

import nextstep.jwp.ApplicationContext;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.presentation.Controller;

public class HandlerMapping {

    private HandlerMapping() {
    }

    public static Handler getHandler(HttpRequest request, ApplicationContext applicationContext) {

        Controller controller = applicationContext.getController(request);

        if (controller == null) {
            return new FileAccessHandler();
        }

        return new HttpRequestHandler(controller);
    }
}
