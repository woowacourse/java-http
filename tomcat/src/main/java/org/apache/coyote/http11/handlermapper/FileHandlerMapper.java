package org.apache.coyote.http11.handlermapper;

import java.util.List;
import org.apache.coyote.http11.handler.FileHandler.FileHandler;
import org.apache.coyote.http11.handler.FileHandler.LoginPageHandler;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class FileHandlerMapper implements HandlerMapper {

    private static final List<Handler> FILE_URI_HANDLER = List.of(new LoginPageHandler(), new FileHandler());
    
    @Override
    public Handler mapHandler(HttpRequest httpRequest) {
        return FILE_URI_HANDLER.stream()
                .filter(handler -> handler.canHandle(httpRequest))
                .findFirst()
                .orElse(null);           
    }
}
