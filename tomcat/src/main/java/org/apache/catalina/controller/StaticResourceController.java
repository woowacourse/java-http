package org.apache.catalina.controller;

import static org.apache.catalina.controller.util.ResourceFinder.findResource;

import java.net.URL;
import java.util.Optional;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class StaticResourceController extends AbstractController {

    private static final String STATIC_RECOURSE_PATH = "static";
    public static final String EMPTY_BODY = "";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestPath requestPath = httpRequest.getRequestPath();

        URL resourceUrl = StaticResourceController.class.getClassLoader()
                .getResource(STATIC_RECOURSE_PATH + requestPath.getRequestPath());
        return resourceUrl != null;
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String resource = findResource(httpRequest.getRequestPath().getRequestPath());
        Optional<ContentType> contentType = findResourceExtension(httpRequest.getRequestLine());

        httpResponse.init(resource, contentType.orElse(null),
                contentType.isPresent() ? HttpStatus.OK : HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.init(EMPTY_BODY, ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }

    private Optional<ContentType> findResourceExtension(final RequestLine requestLine) {
        String extension = requestLine.getRequestPathExtension();
        return ContentType.findContentType(extension);
    }
}
