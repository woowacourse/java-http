package org.apache.catalina.resolver;

import java.io.IOException;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resource.ViewResourceLoader;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class ResourceHandler {

    private static final String HEADER_KEY_LOCATION = "Location";
    private final ViewResourceLoader viewResourceLoader;

    public ResourceHandler(ViewResourceLoader viewResourceLoader) {
        this.viewResourceLoader = viewResourceLoader;
    }

    public void resolve(String resourcePath, Http11Response response) {
        if (response.getHttpStatus() == HttpStatus.Found) {
            response.addHeader(HEADER_KEY_LOCATION, resourcePath);
            return;
        }
        try {
            byte[] responseBody = viewResourceLoader.getResponseBody(resourcePath);
            response.setBody(responseBody);
        } catch (IOException e) {
            throw new PathNotFoundException(response);
        }
    }
}
