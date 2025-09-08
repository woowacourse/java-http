package org.apache.catalina.resolver;

import java.io.IOException;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resource.ViewResourceLoader;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class ViewResolver {

    private final ViewResourceLoader viewResourceLoader;

    public ViewResolver(ViewResourceLoader viewResourceLoader) {
        this.viewResourceLoader = viewResourceLoader;
    }

    public void resolve(String resourcePath, Http11Response response) {
        try {
            byte[] responseBody = viewResourceLoader.getResponseBody(resourcePath);
            response.setBody(responseBody);
        } catch (IOException e) {
            response.setState(HttpStatus.NOT_FOUND);
            throw new PathNotFoundException(e);
        }
    }
}
