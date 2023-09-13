package org.apache.catalina.controller;

import static java.util.Objects.requireNonNull;
import static org.apache.catalina.controller.StaticResourceUri.*;
import static org.apache.coyote.http11.response.ResponseContentType.TEXT_HTML;

import org.apache.catalina.util.FileLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatusCode;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String resource = FileLoader.load(RESOURCE_DIRECTORY + DEFAULT_PAGE.getUri());

        response.setStatusCode(HttpStatusCode.OK)
                .addContentTypeHeader(TEXT_HTML.getType())
                .addContentLengthHeader(requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }
}
