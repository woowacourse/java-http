package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.coyote.http11.dto.HttpRequest;

public class HttpResponseDispatcher implements Handler {

    private final List<Handler> chain;

    public HttpResponseDispatcher(List<Handler> chain) {
        this.chain = chain;
    }

    @Override
    public boolean canHandle(HttpRequest req) {
        return true;
    }

    @Override
    public void handle(HttpRequest request, OutputStream outputStream) throws IOException {
        for (Handler handler : chain) {
            if (handler.canHandle(request)) {
                handler.handle(request, outputStream);
                return;
            }
        }

        Responses.notFound(outputStream, request.version());
    }
}
