package org.apache.coyote.http11.request;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.MimeType;

public class RequestProcessor {

    private final ResourceLocator resourceLocator;

    public RequestProcessor(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public HttpResponse process(HttpRequest request) {
        String requestURI = request.getRequestURI();
        Resource resource = resourceLocator.findResource(requestURI);
        return new HttpResponse(
                HttpStatus.OK,
                resource.getMimeType(),
                resource.getData());
    }
}
