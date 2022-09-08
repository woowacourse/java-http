package org.apache.coyote.http11.handlermapper;

import java.util.List;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.FileHandler.FileController;
import org.apache.coyote.http11.controller.FileHandler.LoginPageHandler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class FileHandlerMapper implements HandlerMapper {

    private static final List<Controller> FILE_URI_HANDLER = List.of(new LoginPageHandler(), new FileController());

    @Override
    public Controller mapHandler(HttpRequest httpRequest) {
        return FILE_URI_HANDLER.stream()
                .filter(handler -> handler.canHandle(httpRequest))
                .findFirst()
                .orElse(null);
    }
}
