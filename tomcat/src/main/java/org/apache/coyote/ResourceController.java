package org.apache.coyote;

import org.apache.catalina.ResourceContentTypeResolver;
import org.apache.catalina.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.net.URL;

import static org.apache.coyote.http11.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

public class ResourceController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return "GET".equals(httpRequest.getMethod());
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final String resourceName = httpRequest.getTarget();
        final URL resourceUrl = ResourceReader.getResourceUrl(resourceName);
        if (resourceUrl == null) {
//            throw new IllegalArgumentException("The resource corresponding to the request does not exist");
            final URL resource404 = ResourceReader.getResourceUrl("404.html");
            final String responseBody = ResourceReader.read(resource404);
            httpResponse.setStatusCode(NOT_FOUND);
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponse.setBody(responseBody);
            return;
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

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
