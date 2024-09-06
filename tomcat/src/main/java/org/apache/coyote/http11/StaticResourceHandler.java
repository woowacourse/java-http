package org.apache.coyote.http11;

import java.net.URI;
import java.net.URL;

public class StaticResourceHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URL resource = getClass().getClassLoader().getResource("static/" + httpRequest.getUri());

        return resource != null;
    }

    @Override
    public ForwardResult forward(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();

        return new ForwardResult(uri.getPath(), HttpStatus.OK);
    }
}
