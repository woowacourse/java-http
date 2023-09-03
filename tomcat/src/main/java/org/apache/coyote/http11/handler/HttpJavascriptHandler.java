package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.FileHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class HttpJavascriptHandler implements HttpRequestHandler {

    public static final String JAVASCRIPT_PATH_PREFIX = "static/js/";

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.isJavascriptRequest();
    }

    @Override
    public void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .contentType("text/javascript")
                .responseBody(new FileHandler().readFromResourcePath(JAVASCRIPT_PATH_PREFIX + httpRequest.getEndPoint()))
                .build(outputStream);
        httpResponse.flush();
    }
}
