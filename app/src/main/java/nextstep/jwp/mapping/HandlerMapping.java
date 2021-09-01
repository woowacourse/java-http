package nextstep.jwp.mapping;

import nextstep.jwp.ApplicationContext;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.presentation.Controller;

public class HandlerMapping {

    private final Handler fileAccessHandler = new FileAccessHandler();

    private final ApplicationContext applicationContext;

    public HandlerMapping(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Handler getHandler(HttpRequest request) {
        Controller controller = applicationContext.getController(request);

        if(controller == null) {
            return fileAccessHandler;
        }
        return new HttpRequestHandler(controller);
    }
}
