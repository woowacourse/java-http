package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class CssRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET && httpRequest.getRequestUrl()
                .endsWith(".css");
    }

    @Override
    public void response(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        URL resource = getClass().getClassLoader()
                .getResource("static" + httpRequest.getRequestUrl());
        Path resourcePath = Path.of(resource.getPath());

        httpResponse.ok()
                .writeStaticResource(resourcePath);
    }
}
