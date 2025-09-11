package org.apache.catalina.web.resolver;

import java.io.IOException;
import org.apache.catalina.Resolver;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resources.ResourceManager;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;


public class ViewResolver implements Resolver {

    private static final String HEADER_KEY_LOCATION = "Location";
    private final ResourceManager resourceManager;

    public ViewResolver(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void resolve(String resourcePath, Http11Response response) {
        if (response.getHttpStatus() == HttpStatus.Found) {
            response.addHeader(HEADER_KEY_LOCATION, resourcePath);
            return;
        }
        try {
            byte[] responseBody = resourceManager.getResponseBody(resourcePath);
            response.setBody(responseBody);
        } catch (IOException e) {
            throw new PathNotFoundException(response);
        }
    }
}
