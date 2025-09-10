package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherHandler {

    private final List<Handler> chain;

    public DispatcherHandler(List<Handler> chain) {
        this.chain = chain;
    }

    public void dispatch(HttpRequest request, OutputStream outputStream) throws IOException {
        HttpResponse response = new HttpResponse(request.version());

        for (Handler handler : chain) {
            if (handler.canHandle(request)) {
                handler.handle(request, response);
                break;
            }
        }
        response.commit(outputStream);
    }
}
