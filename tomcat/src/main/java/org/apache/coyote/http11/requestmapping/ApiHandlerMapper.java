package org.apache.coyote.http11.requestmapping;

import java.util.List;
import org.apache.coyote.http11.controller.apicontroller.LoginController;
import org.apache.coyote.http11.controller.apicontroller.RegisterApiController;
import org.apache.coyote.http11.controller.apicontroller.RegisterPageApiController;
import org.apache.coyote.http11.controller.apicontroller.RootApiHandler;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class ApiHandlerMapper implements RequestMapper {

    private static final List<Controller> API_HANDLERS = List.of(
            new RootApiHandler(), new LoginController(),
            new RegisterPageApiController(), new RegisterApiController()
    );

    @Override
    public Controller mapHandler(HttpRequest httpRequest) {
        return API_HANDLERS.stream()
                .filter(apiHandler -> apiHandler.canHandle(httpRequest))
                .findFirst()
                .orElse(null);
    }
}

