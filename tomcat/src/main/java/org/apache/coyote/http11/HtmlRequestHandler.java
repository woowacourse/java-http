package org.apache.coyote.http11;

import java.net.URL;
import java.nio.file.Path;

public class HtmlRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET &&
                httpRequest.getRequestUrl()
                        .endsWith(".html");
    }

    @Override
    public void response(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        URL resource = getClass().getClassLoader()
                .getResource("static" + httpRequest.getRequestUrl());
        Path resourcePath = Path.of(resource.getPath());

        httpResponse.ok()
                .writeStaticResource(resourcePath);
    }
}
