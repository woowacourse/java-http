package org.apache.coyote.http11.requestmapping;

import java.util.List;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.filecontroller.FileController;
import org.apache.coyote.http11.controller.filecontroller.LoginPageController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class FileHandlerMapper implements RequestMapper {

    private static final List<Controller> FILE_URI_HANDLER = List.of(new LoginPageController(), new FileController());

    @Override
    public Controller mapHandler(HttpRequest httpRequest) {
        return FILE_URI_HANDLER.stream()
                .filter(handler -> handler.canHandle(httpRequest))
                .findFirst()
                .orElse(null);
    }
}
