package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceHandler extends AbstractRequestHandler {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String pathWithExtension = String.format("%s.%s", request.getPath(), request.getExtension());

        response.setStaticResourceResponse(pathWithExtension);
        response.write();
    }
}
