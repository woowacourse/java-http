package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class IndexCSSHandler implements HttpRequestHandler {

    public static final String CSS_PATH_PREFIX = "static/css/";

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/css/styles.css");
    }

    @Override
    public void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType("text/css")
                .responseBody(new FileHandler().readFromResourcePath(CSS_PATH_PREFIX + httpRequest.getEndPoint()))
                .build(outputStream);
        httpResponse.flush();
    }
}
