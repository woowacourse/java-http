package org.apache.coyote.http11.handlermapper;

import org.apache.coyote.http11.handler.FileHandler.FileHandler;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class FileHandlerMapper implements HandlerMapper {

    private static final FileHandler FILE_URI_HANDLER = new FileHandler();


    @Override
    public Handler mapHandler(HttpRequest httpRequest) {
        if (FILE_URI_HANDLER.canHandle(httpRequest)) {
            return FILE_URI_HANDLER;
        }
        return null;
    }
}
