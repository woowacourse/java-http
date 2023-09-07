package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class HttpAssetHandler implements HttpRequestHandler {

    public static final String ASSETS_PATH_PREFIX = "static/assets/";

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.isAssetRequest();
    }

    @Override
    public void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType("text/javascript")
                .responseBody(new FileHandler().readFromResourcePath(ASSETS_PATH_PREFIX + httpRequest.getEndPoint()))
                .build(outputStream);
        httpResponse.flush();
    }
}
