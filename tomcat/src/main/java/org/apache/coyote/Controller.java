package org.apache.coyote;

import org.apache.catalina.ResourceContentTypeResolver;
import org.apache.catalina.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.net.URL;

import static org.apache.coyote.http11.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

public abstract class Controller {

    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (!canHandle(httpRequest)) {
            throw new IllegalArgumentException("There is no controller to handle.");
        }

        final String method = httpRequest.getMethod();
        if ("GET".equals(method)) {
            doGet(httpRequest, httpResponse);
        }
        if ("POST".equals(method)) {
            doPost(httpRequest, httpResponse);
        }
        // TODO: exception - invalid http method
    }

    protected void handleResource(
            final String resourceName,
            final HttpRequest httpRequest,
            final HttpResponse httpResponse
    ) throws IOException {
        final URL resourceUrl = ResourceReader.getResourceUrl(resourceName);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("The resource corresponding to the request does not exist");
        }

        final ResourceContentTypeResolver resourceContentTypeResolver = new ResourceContentTypeResolver();
        final String contentType = resourceContentTypeResolver.getContentType(httpRequest.getHeaders(), resourceName);

        if (contentType == null) {
            throw new IllegalArgumentException("Content type is not supported.");
        }

        final String responseBody = ResourceReader.read(resourceUrl);
        httpResponse.setStatusCode(OK);
        httpResponse.addHeader(CONTENT_TYPE, contentType);
        httpResponse.setBody(responseBody);
    }

    public abstract boolean canHandle(final HttpRequest httpRequest);

    public abstract void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;
    public abstract void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;
}
