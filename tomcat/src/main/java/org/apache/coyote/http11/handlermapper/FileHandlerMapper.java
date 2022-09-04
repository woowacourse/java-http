package org.apache.coyote.http11.handlermapper;

import org.apache.coyote.http11.handler.FileHandler.FileUriHandler;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class FileHandlerMapper implements HandlerMapper{

    private static final FileUriHandler FILE_URI_HANDLER = new FileUriHandler();


    @Override
    public Handler mapHandler(HttpRequest httpRequest) {
        if (FILE_URI_HANDLER.canHandle(httpRequest)) {
            return FILE_URI_HANDLER;
        }
        return null;
    }
}
