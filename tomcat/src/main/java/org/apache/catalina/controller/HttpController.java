package org.apache.catalina.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.common.ResourceContentTypeResolver;
import org.apache.coyote.http11.common.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.net.URL;

import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

public abstract class HttpController implements Controller {

    @Override
    public void init() {
    }

    @Override
    public void destory() {
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            if (!canHandle(httpRequest)) {
                throw new HttpException("There is no controller to handle.");
            }
            final String method = httpRequest.getMethod();
            if ("GET".equals(method)) {
                doGet(httpRequest, httpResponse);
            }
            if ("POST".equals(method)) {
                doPost(httpRequest, httpResponse);
            }
        } catch (final Exception e) {
            httpResponse.setStatusCode(NOT_FOUND);
            httpResponse.setBody(e.getMessage());
        }
    }

    protected void handleResource(
            final String resourceName,
            final HttpRequest httpRequest,
            final HttpResponse httpResponse
    ) throws IOException {
        final URL resourceUrl = ResourceReader.getResourceUrl(resourceName);
        if (resourceUrl == null) {
            throw new HttpException("The resource corresponding to the request does not exist");
        }

        final ResourceContentTypeResolver resourceContentTypeResolver = new ResourceContentTypeResolver();
        final String contentType = resourceContentTypeResolver.getContentType(httpRequest.getHeaders(), resourceName);

        if (contentType == null) {
            throw new HttpException("Content type is not supported.");
        }

        final String responseBody = ResourceReader.read(resourceUrl);
        httpResponse.setStatusCode(OK);
        httpResponse.addHeader(CONTENT_TYPE, contentType);
        httpResponse.setBody(responseBody);
    }

    public abstract boolean canHandle(final HttpRequest httpRequest);

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
    }
}
